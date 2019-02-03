# Info System V1 - Resident  Device

### Purpose
<img src="https://github.com/ishaanjav/InfoSystemV1-Resident_Device/blob/master/Front%20Page.PNG" align="right" width="300">

**The purpose of this Android app is to serve as part of a system of 3 applications that inform patients with diseases such as Alzheimer's or dementia about visitors coming to the house since they have difficulties recognizing faces and objects.** In addition to viewing the visitor's name, the Alzheimer's patient will also receive a picture of the visitor, their contact details, and other information about them such as their relation to the resident and an extended description of themself. 

Patients with Alzheimer's suffer from memory loss due to the death of brain cells from a plaque containing beta amyloid. This app is meant to help the Alzheimer's patient not only see that a visitor has logged in, but also *understand* who they are by providing them with useful information.

### Possible Applications:
- **Homes of individuals with Alzheimer's or dementia:** The Information System would be very effective at individual homes of patients. The patient's caretaker would be able to approve visitors' accounts before they start using them and both the patient and caretaker would be aided: the patient by getting information about visitors and the caretaker by being able to view a log of events in their [own app](https://github.com/ishaanjav/InfoSystemV1-Caretaker_Device).
- **Old-age homes:** The Information System can serve a great purpose in residential homes for the elderly because it would provide a method of authenticating visitors before they could enter the home. Additionally, the social workers would be able to verify visitors' accounts.

###### This app is a part of a system of 3 other apps that function together to accomplish the processes and purposes described above. It is **not** a stand-alone app and is meant to be used in collaboration with the two other apps that can be found at these repositories: [CARETAKER APP REPOSITORY](https://github.com/ishaanjav/InfoSystemV1-Caretaker_Device), [VISITOR DEVICE APP REPOSITORY](https://github.com/ishaanjav/InfoSystemV1-Visitor_Device).

-----
# Usage
<img src="https://github.com/ishaanjav/InfoSystemV1-Resident_Device/blob/master/Demo.gif" align="right" width="315">

Since this app is intended for those with Alzheimer's or dementia, it is very simple to use and only has one page. On this page, the resident can **view information about each visitor in a `ListView` that also has the person's picture** and a phone icon for calling the person, and email icon for emailing them.

Additionally, in the top `ActionBar` of the app, there is a danger/warning symbol which the patient can click to immediately contac their caretaker in an emergency. Residents can also have the option for using the app's **Text-To-Speech-From-Image feature** or **Image Labelling feature** because in addition to not being able to recognize faces, patient's with Alzheimer's have trouble recognizing objects and reading. 

## Features of this App
- Viewing **account details and images** of all visitors/users.
- Easy method to **contact visitors/users**.
- **Text-To-Speech** for reading users' account information to the patient.
- Receive notifications when **visitors sign in**.
- Receive notifications when **visitors fail to sign in** and get the option to call the caretaker with a press of a button.
- Allowing the caretaker to track the resident's location, *in the case that they get lost*, by sending the device's location to Firebase when the caretaker opens the Location Tracking page in [their app.](https://github.com/ishaanjav/InfoSystemV1-Caretaker_Device)
- **Action Bar Icons**:
   * Easy method of **contacting the caretaker**
   * **Text-To-Speech-From-Image** *, images from camera*
   * **Image Labelling** *, images from camera*
-----
# Setup
**To use the app:** you simply have to clone this repository, open it in Android Studio, and run it on your Android device. 
**Once you have installed the app on your device, you can start using it without having to follow any additional steps.**

Unlike some of my [other repositories](https://github.com/ishaanjav), this app does not use APIs like the Face API or Kairos's SDK for Android. However, it does use **Firebase**, *which you will not need to worry about because the Firebase connection is already in `google-services.json`*. 

-----
## Other Information System Apps
This app is not meant to be a stand-alone app and works alongside 2 other apps as part of the "Information System". The apps function together to provide the patient and their caretaker with information about visitors and visits to the house. A list of the apps is below:

- [**Visitor Device App Repository**](https://github.com/ishaanjav/InfoSystemV1-Visitor_Device): The purpose of this Android application is to serve as part of a system of 3 apps that collect information about visits to the house. This app, in particular, is used to validate visitors who are logging in to the system. Once a visitor has signed into the app, the Alzheimer's patient and their caretaker are notified.
- [**Caretaker App Repository**](https://github.com/ishaanjav/InfoSystemV1-Caretaker_Device): The purpose of this Android application is to serve as part of a system of 3 apps that collect information about visits to the house. This app, in particular, is the app for the patient's caretaker who can view events that the system logs, approve or decline accounts of new visitors, track the patient if they get lost, and analyze their emotions.




