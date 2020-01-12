# ClarifEye

A mobile application designed to aid the visually impaired by verbally identifying their surroundings as well as reading aloud text on objects or documents.

Developed at nwHacks 2020 (24 hrs).

## Inspiration
From the very beginning our team knew that we wanted to design something that would help improve people's lives. While brainstorming and discussing possible project ideas, we soon found ourselves gravitating towards the field of computer vision as it was a topic we were all interested in exploring. Through further dicussion, one of our team members shared their personal experience about their grandfather who struggled daily with being visually impaired, which guided our team to what we were going to try and create. The idea of <i>ClarifEye</i> was born.

## About The Project
<i>ClarifEye</i> is specifically designed with accessibility for the visually impaired in mind. We spoke with multiple mentors who have had personal experience with the needs of visually impaired people and how they use mobile phones so that we could best accommodate their needs when developing our application. As a result, <i>ClarifEye</i> places emphasis on audio and haptic cues to aid the user as they interact with the app through extremely simple swiping or tapping gestures. <i>ClarifEye</i> utilizes the power of Google's Cloud Vision API to analyze the current image from the user's camera and synthesizes an audio description of that image through our original algorithm. Additionally, the application provides a text/document reading service to its users using similar computer vision technology.

## Accessible Controls

<pre><code>Swipe Left = Change the mode to Read Document Mode
Swipe Right = Change the mode to Describe Image Mode
Swipe Down = Stop the audio
Single Tap = Take a photo, and perform the currently selected function</code></pre>

## What We Learned

While working on this project our team learned a variety of new and useful skills. Such as:

<ul>
<li>Using Google Cloud APIs, specifically the Google Cloud Vision API.</li>
<li>Parsing a JSON object.</li>
<li>Building an Android app.</li>
<li>Interfacing with the camera of a mobile phone and designing a custom camera UI.</li>
</ul>

## Future Work

<i>ClarifEye</i> has a lot of possible improvements to be made. One improvement in particular that we would like to try is to utilize the power of machine learning with a more detailed and customized dataset to provide better descriptions to the user. Another useful feature would be to support multiple languages.
