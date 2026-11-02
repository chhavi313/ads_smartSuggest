
const searchInput = document.getElementById("searchInput");
const suggestionsBox = document.getElementById("suggestions");
const clearButton = document.getElementById("clearButton");
const selectionMessage = document.getElementById("selectionMessage");

const newWordInput = document.getElementById("newWordInput");
const addWordButton = document.getElementById("addWordButton");
const addMessage = document.getElementById("addMessage");

let searchTimer;

searchInput.addEventListener("input", () => {
    clearTimeout(searchTimer);

    const prefix = searchInput.value.trim();

    selectionMessage.textContent = "";

    if (!prefix) {
        suggestionsBox.style.display = "none";
        suggestionsBox.innerHTML = "";
        return;
    }

    searchTimer = setTimeout(() => {
        getSuggestions(prefix);
    }, 150);
});

async function getSuggestions(prefix) {
    try {
        const response = await fetch(
            `/api/suggest?prefix=${encodeURIComponent(prefix)}`
        );

        if (!response.ok) {
            throw new Error("Unable to fetch suggestions");
        }

        const suggestions = await response.json();
        displaySuggestions(suggestions);

    } catch (error) {
        suggestionsBox.style.display = "block";
        suggestionsBox.innerHTML =
            '<div class="suggestion-item">Server connection failed. Start the Java server.</div>';
    }
}

function displaySuggestions(suggestions) {
    suggestionsBox.innerHTML = "";
    suggestionsBox.style.display = "block";

    if (suggestions.length === 0) {
        suggestionsBox.innerHTML =
            '<div class="suggestion-item">No matching words found.</div>';
        return;
    }

    suggestions.forEach((item, index) => {
        const row = document.createElement("div");
        row.className = "suggestion-item";

        const word = document.createElement("span");
        word.className = "word";
        word.textContent = `${index + 1}. ${item.word}`;

        const frequency = document.createElement("span");
        frequency.className = "frequency";
        frequency.textContent = `${item.frequency} searches`;

        row.appendChild(word);
        row.appendChild(frequency);

        row.addEventListener("click", () => selectSuggestion(item.word));

        suggestionsBox.appendChild(row);
    });
}

async function selectSuggestion(word) {
    try {
        const response = await fetch("/api/select", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `word=${encodeURIComponent(word)}`
        });

        if (!response.ok) {
            throw new Error("Could not update frequency");
        }

        searchInput.value = word;
        suggestionsBox.style.display = "none";
        selectionMessage.textContent =
            `✓ You selected "${word}". Its search frequency has been updated.`;

    } catch (error) {
        selectionMessage.textContent = "Unable to update search frequency.";
    }
}

clearButton.addEventListener("click", () => {
    searchInput.value = "";
    suggestionsBox.innerHTML = "";
    suggestionsBox.style.display = "none";
    selectionMessage.textContent = "";
    searchInput.focus();
});

async function addWord() {
    const word = newWordInput.value.trim();

    if (!word) {
        addMessage.textContent = "Please enter a word.";
        return;
    }

    try {
        const response = await fetch("/api/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `word=${encodeURIComponent(word)}`
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Unable to add word");
        }

        addMessage.textContent = `✓ ${result.message}`;
        newWordInput.value = "";

    } catch (error) {
        addMessage.textContent = error.message;
    }
}

addWordButton.addEventListener("click", addWord);

newWordInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        addWord();
    }
});
