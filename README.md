# Wordle Hardest Guessable Word

Welcome to the Wordle Game Evilizer, a program designed to make the Wordle game as challenging as possible by selecting the most difficult words for the player to guess. This program takes a list of secrets and a user's word guess as input, calculating the difficulty of each word based on the feedback provided in the game.

## Features

Difficulty Calculation: The program calculates the difficulty of each word based on the feedback provided in the Wordle game. It considers green letters (correct) and orange letters (present but in the wrong place) to determine the score.
Evil Word Selection: The goal is to make the Wordle game more evil by selecting words that are the most difficult to guess. The difficulty is determined by finding words with the lowest scores based on the users first guess and the letters it contains.
Frequency Analysis: To enhance the evilness, the program calculates the frequency of each letter in the secrets list. Letters that occur less frequently in the secrets list are considered more obscure and are prioritized in word selection.

## Usage

To use the Wordle Game Evilizer, follow these steps:

Provide a list of secrets, representing all possible words in the Wordle game (one is provided in the repo).
Input the user's first guess as a reference for word selection.
Run the ranked_evil function with the secrets list and the user's first guess:

    ranked_evil(secrets, first_guess)
    
This function selects words that are furthest away from the user's first guess, considering both difficulty and letter obscurity.
Explore the program to maximize the challenge in the Wordle game!
