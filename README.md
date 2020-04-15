## Android Change Logs View
100% Customizable View to display your application Release Notes

#### Preview

| ![](docs/showAlways.gif) | ![](docs/showAlwaysWithFooter.gif) |
| ------------- | ------------- |

#### Installation
Add it in your root build.gradle at the end of repositories:
````groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
````
Add the dependency
````groovy
dependencies {
    implementation 'com.github.xeinebiu:android_view_changelogs:1.0.2'
}
````

#### Builder

##### Properties
> asBottomSheet() - Show Releases on a Bottom Sheet

> asDialog() - Show Releases on a Dialog

> asView(ViewGroup) - Show Releases on a ViewGroup

> withLimit(Int) - Limit amount of Release notes to show

> withFooter(Int) - Display [View] as Footer

> withReleaseDivider(Int) - Set the [layoutId] to use as divider between Release's

> withReleaseNote(Int) - Set the [layoutId] to use for Release Note

> withReleaseTitle(Int) - Set the [layoutId] to use for Release Title

> withHeaderText(String) - Set a text to display on the header

> withHeader(Int) - Set the [layoutId] for Header

----
Start creating the ChangeLogManager using the Builder with default options
````kotlin
ChangeLogManager.Builder.with(this, getString(R.string.changelogs))
                .asDialog()
                .build()
                .show()
````

##### Follow the application example for more Usage Examples

#### Author
> xeinebiu
