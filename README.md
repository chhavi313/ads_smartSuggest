# SmartSuggest – Intelligent Search Autocomplete and Suggestion Ranking System

SmartSuggest is a web-based intelligent search autocomplete system that provides real-time word suggestions as users type. It uses a **Trie data structure** for efficient prefix-based searching and a **Custom Max Heap** to rank suggestions based on word frequency.

The system also learns from user selections by updating word frequencies, helping frequently selected words appear higher in future suggestions.

## Live Demo

**[Try SmartSuggest](https://ads-smartsuggest.onrender.com/)**

*Note: The application is hosted on Render's free instance, which may take some time to respond after periods of inactivity.*

## Features

- **Real-Time Autocomplete:** Displays suggestions while the user types.
- **Trie-Based Searching:** Efficiently retrieves words matching a given prefix.
- **Frequency-Based Ranking:** Ranks suggestions according to their usage frequency.
- **Custom Max Heap:** Retrieves the highest-priority suggestions efficiently.
- **Learning from User Selections:** Updates word frequency when a suggestion is selected.
- **Add New Words:** Allows users to add new words to the dictionary.
- **Persistent Local Dictionary:** Stores words and their frequencies in a text file.
- **Web-Based Interface:** Provides an interactive frontend connected to a Java backend.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend development |
| HTML5 | Webpage structure |
| CSS3 | User interface and styling |
| JavaScript | Frontend interactions |
| Trie | Prefix-based searching |
| Max Heap | Suggestion ranking |
| HashMap | Efficient character and word mapping |
| HTTP Server | Backend and frontend communication |
| Docker | Application containerization |
| Render | Cloud deployment |

## Data Structures and Algorithms

### 1. Trie
A Trie is used to store dictionary words character by character. It allows efficient prefix-based searching without scanning the entire dictionary.

### 2. Custom Max Heap
A custom Max Heap is implemented to prioritize suggestions based on word frequency. Words with higher frequencies receive higher priority, with alphabetical ordering used to break ties.

### 3. HashMap
HashMaps are used to manage child nodes in the Trie and support efficient lookups.

## Project Structure

```text
SmartSuggest/
│
├── src/
│   ├── TrieNode.java
│   ├── Trie.java
│   ├── Suggestion.java
│   ├── MaxHeap.java
│   ├── SuggestionEngine.java
│   ├── SmartSuggestServer.java
│   └── Main.java
│
├── public/
│   ├── index.html
│   ├── style.css
│   └── script.js
│
├── dictionary.txt
├── Dockerfile
└── README.md
```

## How It Works

1. The dictionary is loaded into the Trie when the application starts.
2. The user enters a search prefix.
3. The frontend sends the prefix to the Java backend.
4. The Trie retrieves words matching the prefix.
5. The Max Heap ranks the matching words based on frequency.
6. The top suggestions are returned to the frontend.
7. When a user selects a suggestion, its frequency is updated.
8. Users can also add new words to the dictionary.

## Installation and Setup

### Prerequisites

- Java JDK 11 or later
- Git
- A web browser

### 1. Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

### 2. Navigate to the Project Directory

```bash
cd SmartSuggest
```

### 3. Compile the Java Files

```bash
javac --add-modules jdk.httpserver -d out src/*.java
```

### 4. Run the Application

```bash
java --add-modules jdk.httpserver -cp out SmartSuggestServer
```

### 5. Open the Application

Visit:

```text
http://localhost:8081
```

If port 8081 is unavailable, you can specify another port using the `PORT` environment variable.

For Windows CMD:

```cmd
set PORT=8082
java --add-modules jdk.httpserver -cp out SmartSuggestServer
```

Then visit `http://localhost:8082`.

## Deployment

SmartSuggest is deployed using Docker on Render.

- **Containerization:** Docker
- **Hosting Platform:** Render
- **Deployment Branch:** main

Live URL: https://ads-smartsuggest.onrender.com/

## Future Enhancements

- Personalized suggestions based on individual search history.
- Improved ranking using additional relevance factors.
- Larger dictionary support.
- Search analytics and usage insights.
- Persistent cloud storage for dictionary updates and frequency data.

## Author

Developed as an academic project demonstrating the practical application of Data Structures and Algorithms in an interactive web-based system.

---

**SmartSuggest – Making Search Smarter, One Prefix at a Time.**
