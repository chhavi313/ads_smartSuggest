
import java.util.ArrayList;
import java.util.List;

public class MaxHeap {

    private List<Suggestion> heap;

    public MaxHeap() {
        heap = new ArrayList<>();
    }

    // Higher frequency gets higher priority.
    // For equal frequencies, alphabetical order is used.
    private boolean hasHigherPriority(
            Suggestion a, Suggestion b) {

        if (a.getFrequency() != b.getFrequency()) {
            return a.getFrequency() > b.getFrequency();
        }

        return a.getWord().compareTo(b.getWord()) < 0;
    }

    // Insert a suggestion into the heap
    public void insert(Suggestion suggestion) {

        heap.add(suggestion);

        int index = heap.size() - 1;

        // Move the new element upwards
        while (index > 0) {

            int parent = (index - 1) / 2;

            if (hasHigherPriority(
                    heap.get(index), heap.get(parent))) {

                swap(index, parent);
                index = parent;

            } else {
                break;
            }
        }
    }

    // Remove and return the highest-priority suggestion
    public Suggestion removeMax() {

        if (heap.isEmpty()) {
            return null;
        }

        Suggestion max = heap.get(0);

        Suggestion last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {

            heap.set(0, last);

            heapifyDown(0);
        }

        return max;
    }

    // Restore Max Heap property
    private void heapifyDown(int index) {

        int size = heap.size();

        while (true) {

            int left = 2 * index + 1;
            int right = 2 * index + 2;

            int largest = index;

            if (left < size &&
                    hasHigherPriority(
                        heap.get(left), heap.get(largest))) {

                largest = left;
            }

            if (right < size &&
                    hasHigherPriority(
                        heap.get(right), heap.get(largest))) {

                largest = right;
            }

            if (largest == index) {
                break;
            }

            swap(index, largest);

            index = largest;
        }
    }

    // Swap two elements
    private void swap(int i, int j) {

        Suggestion temp = heap.get(i);

        heap.set(i, heap.get(j));

        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }
}
