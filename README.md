# Stilt Fox&trade; Utilities
This project is really just a hodge podge of classes used to build other programs. In the future this will probably be
broken into sub categories based on utility like IO features and such. As of now however there's not much here so it
kinda all goes into one place.

## How to use
There are currently no plans to host this code on maven central... this means that you will have to use your local maven
repository or a self-hosted one in order to use this. At Stilt Fox&trade; we use GOGs on a local server to achieve our 
desired development environment. If you do not have, or do not wish to set up a server, as stated above, using something
like "publish to maven local" will get you up and running.
### maven
```xml
<dependancy>
    <groupId>com.stiltfox</groupId>
    <artifactId>StiltFox-Utilities</artifactId>
    <version>0.12.9</version>
</dependancy>
```
### gradle
```groovy
implementation "com.stiltfox:StiltFox-Utilities:0.12.9"
```
## How to Setup Custom Repository
Assuming you have something like Artifactory, Reposilite or JFrog installed you can set up this project to easily use your
custom repository. To do this you need to define three variables in your ```gradle.properties``` file. You will notice
that this project does not have a ```gradle.properties``` file. This is because the repository username and password should
be considered sensitive data. You wouldn't want your passwords and usernames and such uploaded to public GitHub, so
to avoid that we use a private ```gradle.properties``` file that's outside the project.\
\
To access and change this file on "Unix like" systems you want to go to your home directory. You should have a ```.gradle``` folder
in your home directory. If you do not, then you should first install gradle on your local machiene (the folder will then
be created). Once inside your ```.gradle``` folder you'll want to create a ```gradle.properties``` file there.
#### gradle.properties Contents
```
StiltFoxRepositoryUrl=www.INeedABiscuit.com/storingYourJars
StiltFoxRepositoryUsername=SomeGuy
StiltFoxRepositoryPassword=mySuperSecretPassword123
```

Once you save the file you will notice that Gradle will be able to publish to StiltFoxRepository. This logic is handled
by the publishing section in [build.gradle](build.gradle)
```groovy
publishing {
    publications {
        maven(MavenPublication) {
            from components.java
        }
    }
    repositories {
        maven {
            name = "StiltFoxRepository"
            url = property('StiltFoxRepositoryUrl')
            credentials(PasswordCredentials)
            authentication {
                basic(BasicAuthentication)
            }
        }
    }
}
```