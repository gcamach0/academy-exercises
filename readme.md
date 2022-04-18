# Big Data Exercises

This repo contains several common big data exercises.

* **MovieRecommender** Uses Amazon movie reviews sample data   [stanford.edu/data/web-Movies.html](http://snap.stanford.edu/data/web-Movies.html) for a simple movie recommender
* Download the movies.txt.gz file
    
 
 
## Setup

1. Install the JDK 18.0
2. [Download & Install Maven](http://maven.apache.org/download.cgi)
3. Set up the source and the target of the java compiler to your version of the SDK
   ```xml
   <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.target>18</maven.compiler.target>
    <maven.compiler.source>18</maven.compiler.source>
   </properties>
    ```
 
## How to run tests

    #from the repository root
    mvn test
 
