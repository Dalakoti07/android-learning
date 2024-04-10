package main

import (
    "encoding/json"
    "net/http"
    "strings"
)

// Define a data structure
type ResponseData struct {
	Message string `json:"message"`
	LargeData string `json:"largeData"`
}

func main() {
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

    http.ListenAndServe(":8080", nil)
}


