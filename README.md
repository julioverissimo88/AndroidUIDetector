# General info

AndroidDetector is a library/API written in Java that can detect Android smells.

Empirical study: https://pure.tudelft.nl/portal/files/55394009/preprint.pdf

Currently, it supports the detection of the following smels:

supported by **AndroidDetector 1.0**

1. Magic Resource
2. Deep Nested Layout
3. Missing Image
4. Coupled UI Component
5. Suspicious Behavior
6. Longor Repeated Layout
7. Fool Adapter
8. Excessive Use of Fragments
9. UI Component Doing IO
10. No Use of Fragments
11. God Style Resource
12. God String Resource
13. Duplicate Style Attributes
14. Flex Adapter
15. Inappropriate String Reuse
16. Hidden Listener
17. Brain UI Component

## How to build Android Detector

**DetectorUI**

Graphic interface Java Swing

**AndroidDetector**

library/API Java

To run the tool via command-line, you need to invoke the following command:

java -cp Detector-1.0.jar br.com.AndroidDetector.Command [path-to-android-app] [path-to-where-i-want-to save-the results] [smell]

**DetectorApi**

Api Java Spring

https://detector-api.herokuapp.com/

**Example Front End in ReactJS consuming DetectorApi**
https://app-detector.herokuapp.com/

**Endpoints**


case local run replace https://detector-api.herokuapp.com with "http://yourlocalURL"

|  Smell | Endpoint  | Post Parameter(repository)|
| ------------ | ------------ |------------ |
|Magic Resource|https://detector-api.herokuapp.com/MagicResource| Project Git Url|
|Deep Nested Layout|https://detector-api.herokuapp.com/DeepNestedLayout| Project Git Url|
|Missing Image|https://detector-api.herokuapp.com/MissingImage| Project Git Url|
|Coupled UI Component|https://detector-api.herokuapp.com/CoupledUIComponent| Project Git Url|
|Suspicious Behavior|https://detector-api.herokuapp.com/SuspiciousBehavior| Project Git Url|
|Longor Repeated Layout|https://detector-api.herokuapp.com/LongorRepeatedLayout| Project Git Url|
|Fool Adapter|https://detector-api.herokuapp.com/FoolAdapter| Project Git Url|
|Excessive Use of Fragments|https://detector-api.herokuapp.com/ExcessiveUseofFragments| Project Git Url|
|UI Component Doing IO|https://detector-api.herokuapp.com/UIComponentDoingIO| Project Git Url|
|No Use of Fragments|https://detector-api.herokuapp.com/NoUseofFragments| Project Git Url|
|God Style Resource|https://detector-api.herokuapp.com/GodStyleResource| Project Git Url|
|God String Resource|https://detector-api.herokuapp.com/GodStringResource| Project Git Url|
|Duplicate Style Attributes|https://detector-api.herokuapp.com/DuplicateStyleAttributes| Project Git Url|
|Flex Adapter|https://detector-api.herokuapp.com/FlexAdapter| Project Git Url|
|Inappropriate String Reuse|https://detector-api.herokuapp.com/InappropriateStringReuse| Project Git Url|
|Hidden Listener|https://detector-api.herokuapp.com/HiddenListener| Project Git Url|
|Brain UI Component|https://detector-api.herokuapp.com/BrainUIComponent| Project Git Url|



## Contributors

- [Julio Cesar Verissimo dos Santos ](https://github.com/julioverissimo88 "Julio Cesar Verissimo dos Santos ")
- [Rafael Serapilha Durelli](https://github.com/rdurelli "Rafael Serapilha Durelli")
