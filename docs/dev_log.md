# Development Log

## Entry 1
**Date:** 28 July 2026

I started to do the assignment by deciding on the topic that I wanted to do, which is a Subsidy Eligibility Calculator. I then started to do some research online about what a Subsidy Eligibility Calculator is and how it works. I decided to name my system FamilyCareFund.

I then determined the direction of my system, which is to help low-income families check whether they are eligible for government subsidies. Because this FamilyCareFund is not only a subsidy eligibility calculator, it can also help families who do not know how to apply for the subsidies. They can seek help through the system, where NGOs can help them with the application process.

After deciding on the direction of the system, I started to design the flow of the system before moving on to the UML diagram.

## Entry 2
**Date:** 1-4 August 2026

Designing the flow of the system includes deciding what I want this application to do and perform. I asked AI for some ideas, and Claude suggested that I can have 2 different flows, one for admin and one for users.

The admin can log in to the application, where they will see a different dashboard and can perform different tasks. While the user can log in to the system and see the user dashboard, where they can calculate whether they are eligible for the subsidies and do other things in the system.

## Entry 3
**Date:** 5-6 August 2026

Designed the case classes, traits and sealed traits, including the relationship between each class. Also decided how I should store the data and how the classes should work together. After deciding on the structure, I started to draw the UML Diagram.

## Entry 4
**Date:** 7 August 2026
Finished UML Diagram. Started to code, implement build.sbt, code models class, implement all the class code, error handling function and validation input.

## Entry 5
**Date:** 7-8 August 2026

Started coding the AuthService and FileStorage. Implemented the login and registration functions and the data storage for users, households and other information. Tested whether the data could be saved and loaded correctly.

## Entry 6
**Date:** 8-9 August 2026

Continued coding the service classes, including EligibilityChecker and ApplicationChecklist. Implemented the eligibility checking logic for all 6 subsidies and the required documents for each subsidy. Also tested the validation and eligibility results using different household information.

## Entry 7
**Date:** 9 August 2026

Started implementing the ScalaFX Main.scala. First created the main window, reusable styles, buttons, text fields and navigation. Then implemented the landing page, login page and registration page and connected them with the AuthService.

## Entry 8
**Date:** 9-10 August 2026

Continued implementing the ScalaFX scenes. Added the homepage and household information form. The form allows users to enter their household information and add, edit and remove household members.

During testing, I found that some information entered in the household form was lost when navigating to the Add Member page and coming back. I fixed this by using a draft object to temporarily store the information while navigating between the scenes.

## Entry 9
**Date:** 10 August 2026

Implemented the review page, eligibility result page, subsidy browsing and subsidy detail pages. Connected the eligibility result page with the EligibilityChecker so the system can check the household against all 6 subsidies and show the eligible and not eligible subsidies.

## Entry 10
**Date:** 10-11 August 2026
Implemented the application checklist, Print Checklist, NGO Assistance and Admin features. The checklist is generated from the required documents of an eligible subsidy and the Print Checklist function prints the checklist to the CLI/output.

Also implemented the Admin dashboard, registered household viewing and FAQ management. Admin can view registered households and add or delete FAQs, while normal users can only view the FAQs.

## Entry 11
**Date:** 11 August 2026

Finished the main coding part of FamilyCareFund. Checked the navigation between the different scenes and fixed some UI issues, validation messages and button functions. Made sure the main features were connected and working before moving on to final testing.
