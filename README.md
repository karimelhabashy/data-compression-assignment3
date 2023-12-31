# Data Compression — Assignment 3: Huffman Coding

Java GUI application implementing **Huffman coding** — an optimal prefix-free compression algorithm.

## How it works

**Compression:**
1. Reads the input text file and counts character frequencies
2. Builds a min-heap priority queue of `Node` objects (each holding a character and frequency)
3. Repeatedly merges the two lowest-frequency nodes into a parent node until one tree remains
4. Traverses the Huffman tree to assign binary codes (left = `0`, right = `1`) to each character
5. Encodes the input string using these codes and writes the result to `compressed.bin`

**Decompression:**
- Reads bits from the binary file and traverses the stored Huffman tree to recover original characters
- Writes the decoded text to `decompressed.txt`

## Run
```bash
javac src/DataCompressionDialog.java
java DataCompressionDialog
```
