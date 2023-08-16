# PoseDetection

### Note: The code is in pose/main branch

This Android app utilizes the power of machine learning and computer vision to collect and export skeleton data using the MLKit Pose Estimation framework. The collected joint positions are stored in a JSON file, allowing you to analyze and visualize human body poses.

## Overview
This app makes use of the following libraries and frameworks:

## AndroidX Camera Library
The AndroidX Camera library simplifies camera access, image capture, and camera preview streaming. It offers compatibility across various Android versions, providing a unified way to work with the device's camera hardware.

## ML Kit Pose Detection
The ML Kit Pose Detection module empowers the app to identify human body poses within images and video streams. It leverages machine learning models to accurately detect and track joints' positions, contributing to a better understanding of body movements.

## TensorFlow Lite
TensorFlow Lite is a lightweight version of TensorFlow designed for mobile and embedded devices. It enables efficient execution of machine learning models on Android devices. In particular, the tensorflow-lite-gpu variant taps into the device's GPU to enhance inference speed. The tensorflow-lite-support library facilitates smooth integration with Android applications.

## Best Coding Practices
Throughout the development of this app, the following best coding practices were applied:

### Clean and Modular Code: 
The codebase is organized into modular components and follows the principles of clean architecture. This promotes maintainability, testability, and scalability.

### Permissions Handling: 
The app appropriately requests necessary permissions from the user to ensure smooth camera access and data collection.

### Threading and Concurrency: 
Threading and concurrency are managed effectively to prevent blocking the main UI thread. Background threads are used for time-consuming operations such as data collection.

### Error Handling: 
Robust error handling mechanisms are implemented to gracefully handle unexpected situations and provide meaningful error messages to users.

### Resource Management: 
Resources (such as camera and memory) are managed efficiently to prevent leaks and ensure optimal performance.

### UI/UX Design: 
The app adheres to modern UI/UX design principles, providing a user-friendly and intuitive interface. It offers clear instructions and feedback during pose data collection.

### Documentation: 
The code is well-documented with inline comments, explaining complex logic, algorithms, and design decisions. This documentation aids in understanding and maintaining the codebase.

### Code Review: 
The code has undergone rigorous code reviews to identify and address potential issues, improve code quality, and ensure adherence to best practices.

## Usage
Follow these steps to use the PoseDetection Android app:

Open the app and grant the necessary camera and storage permissions.

Press the "Start Pose Estimation" button to initiate the collection of pose data using MLKit Pose Estimation.

The app will capture skeleton data for a predefined duration, as well the classification process is also implemented.

Press the "Stop Pose Estimation" button to conclude data collection.

The collected joint positions will be exported and stored in a JSON file.

You can view, analyze, and visualize the exported joint positions as needed.
