## Compose Navigation Deep Link Sample

This sample project aims to show how to use deep links in the new type-safe version of the Navigation Compose library (2.8.0 and newer).

We have a simple app with two screens:
- Input screen: has inputs for first name, last name, and age. Pressing the Submit button displays the inputs on the second screen
- Result screen: shows the user's inputs from the input screen. 

We want to allow navigating to the result screen directly by leveraging deep links. Clicking on a deep link _https://deeplink.sample.com/result/smith/john?age=28_ should open the app and jump straight to the result screen showing the correct data from the deep link.

 <img src="/screenshots/sample_app_preview.jpg" width="50%" />


 ### Testing

 Run the following adb command in the terminal:
 
     adb shell am start -W -a android.intent.action.VIEW -d "https://deeplink.sample.com/result/smith/john?age=20" com.landomen.composenavigationdeeplinks
