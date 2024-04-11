package main

import (
	"log"
	"os"
)

func main() {
	// Open a new file for writing
	file, err := os.Create("dummy.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// Define the chunk size and the target file size (300 MB)
	chunkSize := 1024 * 1024 // 1 MB
	targetSize := 300 * chunkSize // 300 MB

	// Define a sample text to write
	sampleText := "This is a sample line of text to fill the file.\n"
	// Calculate how many times the sampleText fits into the chunkSize
	repeatsPerChunk := chunkSize / len(sampleText)

	for written := 0; written < targetSize; written += chunkSize {
		// Write the sample text repeatedly to reach the chunk size
		for i := 0; i < repeatsPerChunk; i++ {
			_, err := file.WriteString(sampleText)
			if err != nil {
				log.Fatal(err)
			}
		}
	}

	log.Println("Dummy file creation completed.")
}