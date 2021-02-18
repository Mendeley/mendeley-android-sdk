# This repository has been DEPRECATED by the owner. # 

# This repository will be ARCHIVED by the owner # 
# on 15th March 2021. # 

# This repository will become READ-ONLY # 
# on 15th March 2021. # 

# Mendeley Android SDK #

Version: Alpha-2

**Important note: this is an early pre-release version, and is subject to change.**

## About the SDK ##

The SDK provides a convenient library for accessing the [Mendeley API](http://dev.mendeley.com/)
from Android applications.

## Minimum requirements ##

Android Studio (or another Gradle-based development environment)

Android version: API level 14 (Android 4.0, ICS)

Java version: 1.7


## Getting started ##

### Create a Mendeley app ###

First thing you'll need to do is to create the credentials of a Mendely app. The SDK will use these to let the Mendeley server identify your app.

You can do so by creating a new app in the [Mendeley Applications page](http://dev.mendeley.com/myapps.html)

For creating Android apps, you will only need the client secret and id. The redirect URL won't be used.

### Try the example app ###

A sample app is provided, which illustrates basic use of the library.
To try it, clone the Mendeley SDK repository:

```
git clone https://github.com/Mendeley/mendeley-android-sdk.git
```
Setup the Android Studio project by importing the `build.gradle` file in the directory cloned.

This should create two modules:

* :library, the library of the SDK itself
* :example, the example app

**Configuration of the example app**

Before running the example app you need to configure it to use the credentials you created before.

To do so:

1. Copy the file `/example/src/main/assets/config.properties.sample` to `/example/src/main/assets/config.properties`
2. Paste the app id and secret in the `config.properties` file



## Using the library ##

### Import the Mendeley SDK library to your project ###

Unfortunately, the library is not **yet** available in a public Maven repository.
Hence, you will need to clone the repository and import the module. To do so:

1. If you have not done it yet, clone the repository:
```
git clone https://github.com/Mendeley/mendeley-android-sdk.git
```

2. In Android Studio, while you have your project opened, do `File -> New -> Import Module`
3. In the dialog, in the *Source directory* field, point to the `[DIRECTORY_OF_THE_MENDELEY_REPO]/library` directory. Leave the *Module name* field with its default value or change it if you prefer.
4. In the `build.gradle` file of your app, include the dependency to the library module:

```
dependencies {
    [...]
    compile project(path:': library', configuration:'productionRelease')
}
```

This will copy the source code of the Mendeley SDK into your app under a directory named as the imported module.
If you don't like this, alternativety you can edit the `settings.gradle` file of your project to [import the module using its path in your development machine](https://looksok.wordpress.com/2014/07/12/compile-gradle-project-with-another-project-as-a-dependency/).
With this approach you'll avoid checking the source code of the Mendeley SDK in your VCS, but you will be creating a dependency of your app to another directory in the file system.

Anyway, this is just temporary and we will be publising the Mendeley SDK library in a public Maven repository soon.

Also, you need to provide your app with persmissions to access the Internet by including the following declaraion in the `AndroidManifest.xml`
```
<uses-permission android:name="android.permission.INTERNET" />
```

### Initialization of the SDK ###

You first need to initialize the SDK so that it knows your app id and secret to obtain the OAuth access token needed to query the API. 

You sould do this in the `onCreate` method of your `Application` or main `Activity`.

``` java
@Override
public void onCreate() {
	super.onCreate();
	[....]
    Mendeley.getInstance().init(this, [YOUR_CLIENT_ID], [YOUR_CLIENT_SECRET]);
	[....]
}
```

### Signing the user in and out ###

To sign the user in into Mendeley you can start the signing process from any `Activity` with `Mendeley#signIn(Activity activity, boolean showSignUpScreen);`

To ensure the user is signed in when using your app, you may want to check whether this is true or not in `onResume` and proceed accordingly.

``` java 
@Override
public void onResume() {
    super.onResume();
    [...]
    if (!Mendeley.getInstance().isSignedIn()) {
       Mendeley.getInstance().signIn(this, true);
    }
    [...]
}
```

This will start the sign in flow, which will consist in the SDK opening an `Activity` letting the user enter their username and password. Once this process finishes, the SDK will pass the outcome to the `Activity` that started the flow by calling `Activity#onActivityResult()`.

You need to implement `Activity#onActivityResult()` so that it will pass the result to the SDK along with a `com.mendeley.sdk.Mendeley.SignInCallback`. Your code will eventually get the outcome of the process in the callback.

``` java
Mendeley.SignInCallback signInCallback = new Mendeley.SignInCallback() {
    public void onSignedIn() {
      // success
    }
    
    public void onSignInFailure() {
      // fail
    }
};
    
[...]
    
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (Mendeley.getInstance().onActivityResult(requestCode, resultCode, data, signInCallback)) {
        return;
    }

    [...]
}    
```


You can sign the user out at any time with

``` java
Mendeley.getInstance().signOut();
```




### Performing requests against the Mendeley API ###

The SDK is fully object oriented and HTTP requests are performed by executing the `run()` method of `com.mendeley.sdk.Request` objects.

The requests will query the server, parse the JSON returned by the server and give you a model representing profiles, documents or other classes from the `com.mendeley.sdk.model` package

The esiest way to obtain a `com.mendeley.sdk.Request` object is to use the `RequestsFactory` returned by `Mendeley.getInstance().getRequestsFactory()` 

For example, if you want to obtain a `Request` to query the `Profile` of the currently logged user, do so with

``` java
Request<Profile> req = Mendeley.getInstance().getRequestsFactory().newGetMyProfileRequest() 
```

It is also possible to directly instantiate the `Request` using the `new` operator if you prefer.



#### Asynchronous requests ####

Running requests perform network IO, so you need to execute them in a background thread. 

You can do this by calling the
`Request#runAsync()` method passing one `Request.RequestCallback` to get the outcome of the request. 

This will automatically run the request in an `AsyncTask` and will call the passed callback in the UI thread once it finished.

``` java
Request<Profile> req = Mendeley.getInstance().getRequestFactory().newGetMyProfileRequest();
        
req.runAsync(new Request.RequestCallback<Profile>() {
    @Override
    public void onSuccess(Profile profile, Uri next, Date serverDate) {
        [...]
    }

     @Override
     public void onFailure(MendeleyException mendeleyException) {
        [...]
     }

     @Override
     public void onCancelled() {
        [...]
     }
});

```

The method `Request#runAsync()` is overloaded, letting you pass a custom `Executor` if you want to run the `Request` in a thread different than the default used by `AsyncTask`s.
 
#### Synchronous requests ####

If you need more control over the threading aspects when running your requests, you may want to run them in a synchronous way so that they will run and block the calling thread.
This may be useful if you want to run them from a custom `Loader`, `IntentService`, `Executor` or any other threading mechanism.

To do this simply:

``` java
Request<Profile> req = Mendeley.getInstance().getRequestFactory().newGetMyProfileRequest();
try {
    Response<Profile> response = req.run();
    Profile profile = response.resource;
    [...]
} catch (MendeleyException e) {
    [...]
}
```


## Advance use of the SDK ##

### Avoiding the Mendeley singleton ###

You may not like the idea of using the `Mendeley.getInstance()` method if you want to avoid singletons and use any dependency injection mechanism instead to make you classes easier to test.

In that case, you can completely forget the `Mendeley` singleton and direcly instantiate one implementation of `RequestsFactory` by your own and inject it to your classes.

We recomend that you use the two following classes:

- `com.mendeley.sdk.Mendeley.SharedPreferencesAuthTokenManager` as an implementation of a mechanism that persists the OAuth token in shared preferences
- `com.mendeley.sdk.Mendeley.RequestsFactoryImpl` as an implemetation of a factory of requests.

We recommend you to read the code in `com.mendeley.sdk.Mendeley`, and surely you'll find your way.

### Implementing custom requests ###

The SDK provides implementation for typical requests against the Mendeley API.

If you need to implement any verb or request against any endpoint not covered yet, you can implement your own by extending the `com.mendeley.sdk.Request` class. You may actually extend from the `com.mendeley.sdk.Request.request.HttpUrlConnectionAuthorizedRequest` hierarchy as you'll have most of the work already done.

Also, we accept pull requests.

## Support ##

Email: api@mendeley.com

You can also [open a bug in GitHub](https://github.com/Mendeley/mendeley-android-sdk/issues) if you find any issue.