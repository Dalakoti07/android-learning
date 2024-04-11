package main

import (
	"fmt"
	"io"
	"os"
    "encoding/json"
    "net/http"
    "strings"
)

// Define a data structure
type ResponseData struct {
	Message string `json:"message"`
	LargeData string `json:"largeData"`
}

func uploadFileHandler(w http.ResponseWriter, r *http.Request) {
	// Parse the multipart form
	if err := r.ParseMultipartForm(10 << 20); err != nil { // Max upload of 10 MB files
		fmt.Print("too large file")
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	fmt.Print("upload started")

	// Retrieve the file from posted form-data
	file, handler, err := r.FormFile("file")
	if err != nil {

		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	defer file.Close()

	// Create a file in the server's local filesystem to save the uploaded file
	dst, err := os.Create("./uploads/" + handler.Filename)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer dst.Close()

	// Copy the uploaded file to the destination file
	if _, err := io.Copy(dst, file); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	response := ResponseData{
		Message: "Done",
		LargeData: fmt.Sprintf("File uploaded successfully: %s", handler.Filename),
	}
	json.NewEncoder(w).Encode(response)
	fmt.Print("upload ended")
}


func main() {
	fmt.Printf("hello\n")
	fmt.Print("this prints\n")
    http.HandleFunc("/data", func(w http.ResponseWriter, r *http.Request) {
		// Set the Content-Type header
		w.Header().Set("Content-Type", "application/json")

		// Generate a large string to include in the response
		// Note: Adjust the size of the string as necessary to get close to 100KB
		// The exact size needed might vary depending on the overhead of the JSON structure
		largeString := strings.Repeat("a", 1024*100) // This creates a 100KB string of 'a's

		// Create an instance of ResponseData with the large string
		response := ResponseData{
			Message: "This response is approximately 100KB in size.",
			LargeData: largeString,
		}

		// Encode the ResponseData instance to JSON and send it as the response
		json.NewEncoder(w).Encode(response)
	})

    http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
        w.Write([]byte("Hello"))
    })
	http.HandleFunc("/upload", uploadFileHandler)

    http.ListenAndServe(":8080", nil)
}


