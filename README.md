# Ollama API Integration Project

## Overview

Since we have already covered most of the coding subjects at the university, I have set myself to complete this simple project to continue learning new things about java. 
This project aims to integrate the Ollama API with a simple Java application. 
The project is structured to follow best practices and design patterns to ensure maintainable and scalable code. 
I'll try to use some design patterns such as Singleton, Factory, Strategy, and MVC throughout the development process.

## Project Stages

1. **Project Setup**
    - Setting up the Java project using Maven.
    - Adding necessary dependencies.

2. **API Integration**
    - Creating a service to interact with the Ollama API.
    - Handling API responses.

3. **Basic Functionality**
   -  Implement a simple console interface to prompt the user for input.
   -  Capture user input and send it to the Ollama API.
   -  Parse responses from the Ollama API.
   -  Display the response to the user in the console.
   
4. **Storing Interactions in MongoDB**
   - Storing Interaction in MongoDB.
   - Retrieving all Interactions from MongoDB.
   - Retrieving documents containing a specific string in the question field.
   - Adding a new field to the database to record keywords in the questions.
   
5. **Adding Tags to Interactions**
   - Modify the Interaction Class with a List of strings to record tags.
   - Generate a list of tags from ollama.
   - Update the existing interaction in MongoDB with the generated tags.
   
6. **Refining tags**
   - Generate several prompts to try with ollama3
   - Select the best prompt
   - Update prompt in the code
7. **Next step: Improve code**

