# Planer

## Table of Contents
- [Project Description](#project-description)
- [Technologies Used](#technologies-used)
- [How to install or run the project](#how-to-run-project)

## Project Description
When planning a vacation, consolidating visa, currency, and COVID restriction information is a monstrous task. Here at Planer, we plan on bringing the correct information for travelers, making it easier to choose your next big vacation.

### The problem
Before going on a vacation, the first step is to plan and schedule. Visa requirements, COVID and travel restrictions, weather, and currency are essential factors to take into consideration in determining a destination. However, this information is scattered and hard to sift through on your own, so travelers already have to go through a lot of hassles to gather the correct information. Planer aims to make planning an easy process by bringing the information to travelers.

### App Categories
Travel Planning, Travel Information (Covid Restriction, Exchange Rate, Airport, News)

### Preliminary app idea
Planer is an app designed to help travelers determine their travel destination and make trip planning as easy as possible. We will accomplish this by consolidating visa requirements, COVID/travel restrictions, currency, and weather information on one page. Additionally, we allow users to save their favorite countries. This helps users follow and keep track of a certain country's travel information for their next trip.

#### We will have the following pages.
- Login
- Register to save the home country in the profile
- Consolidated travel Information (Visa, covid, weather, currency, airports, news)
- Favourites to revisit the list of countries saved
- User Profile to change the home country 

## Technologies Used
- Java
- Android
- APIs
- Gradle
- Firebase 

## <a id="how-to-run-project">How to install or run the project</a>

### Prerequisites:

- Have a Git and GitHub account
- Have Java installed
- Install Android Studio

### Cloning the repository:
- Open Command Prompt
- `cd` into the folder you want the repository stored in
- Type: `git clone [https://github.com/Rshokar/Planer]`

### Running the project:
1. Open cloned project in Android Studio
2. Press green play button to run application

## <a id="how-to-use-product">How to use the product (Features)</a>

### Features

#### Authentication
Authentication is handled by firebase and is done after opening the app. 
- Click the sign up button
- Enter information 
- Click submit and you will be navigated to the search page

#### Favourties
To view favourites
- Click favourites in the navigation bar
- User will be redirected to the favourites page
- Here you can view and select your favourite destination

Add Favourite
- Choose a destination from the dropdown
- Click the heart button

#### Score
A score is genereated for each destination and home pair. The score reflects visa requirements, travel restrictions and weather conditions. The higher the score the better.

#### Visa
Shows the type of visa required when entering the country

#### Airport
Shows the biggest airport for that country

#### Weather
Show the current weather in your destination. To show more details click on the weather section

#### Currency
Shows the current currency rate from home to destination country. 

To see how much a different denomination is
- Click currency section 
- User will be redirected to currency page
- Enter quantiy of either home country or destination country currency
