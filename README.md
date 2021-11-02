## Android Change Logs View
100% Customizable & Performance View to display your application Release Notes.

Memory efficient and Strong Performance on long Release Note's list.

----
#### Preview

| ![](docs/show_always.gif) | ![](docs/show_always_footer.gif) |
| ------------- | ------------- |

----
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
    implementation 'com.github.xeinebiu:android_view_changelog:2.1.1'
}
````


----
#### Builder

##### Properties
> withLimit(Int) - Limit amount of Release notes to show

> withFooter(Int) - Display [View] as Footer

> withReleaseDivider(Int) - Set the [layoutId] to use as divider between Release's

> withReleaseNote(Int) - Set the [layoutId] to use for Release Note

> withReleaseTitle(Int) - Set the [layoutId] to use for Release Title

> withHeaderText(String) - Set a text to display on the header

> withHeader(Int) - Set the [layoutId] for Header
----
Show Release Notes using default options
````kotlin
ChangeLogManager.Builder(context, type) { changeLogsApi.get() }
                .build()
                .show()
````


----
Use ```showOnce()``` to show the Release Notes only once per version
````kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
	
        setContentView(R.layout.activity_main)
	
	ChangeLogManager.Builder(context, type) { changeLogsApi.get() }
                .build()
                .showOnce()
    }
}
````


---
#### Supported Formats
> Plain Text
````
# 2.1.1
setLastAppVersionCode after showing the Change Log View

# 2.1.0
Update dependencies

# 2.0.0
Update dependencies
Support coroutines
Code quality improvements
Replace display type from enum to sealed class
Use default behavior on bottom sheet

# 1.2.0
Create the stream only when needed
Code quality
# 1.1.2
Fix limitation
# 1.1.1
Logs are not shown
# 1.1.0
Publish to Git
Support Streams & Static String`s
Bug fixes
# 1.0.0
Hello world App
````

----
##### Follow the demo application for more Usage Examples

#### Author
> xeinebiu
