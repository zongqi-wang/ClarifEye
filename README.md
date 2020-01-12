# ClarifEye
Developed at nwHacks 2020 (24 hours)

A mobile application designed to aid the visually impaired by identifying their surroundings as well as reading aloud text from objects or documents.

ClarifEye is specifically designed with accessibility for the visually impaired in mind. We talked with people with personal experience with how visually impaired people use mobile phones so that we could best accommodate their needs when developing our application. ClarifEye places emphasis on audio and haptic cues to aid the user in interacting with the app through simple swiping or tapping gestures. ClarifEye utilizes the power of Google's Cloud Vision API to analyze the current image from the camera and then synthesizes a description through our novel algorithm which is converted into an audio stream for the user to hear.

## Controls

<pre><code>Swipe Left = Change the mode to Read Document Mode
Swipe Right = Change the mode to Describe Image Mode
Swipe Down = Silence audio
Single Tap = Take a photo, and perform function according to the currently selected mode</code></pre>

## What We Learned

While working on this project we learned a variety of useful skills that we previously had not experienced. Such as:

<ul>
<li>Using Google Cloud APIs, specifically the Google Cloud Vision API.</li>
<li>Parsing a JSON object.</li>
<li>Building an Android app.</li>
<li>Interfacing with the camera of a mobile phone and design a custom camera UI.</li>
</ul>
