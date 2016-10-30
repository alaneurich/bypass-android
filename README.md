# bypass-android
The Android Implementation of this [Bypass Fork](https://github.com/budsmile/bypass/).

In Addition to the original Bypass Android Module this Fork uses a SpanProvider Class that provides all spans
for the different Markdown Tags. For full customization it's possible to replace this class with your
own implementation.

Furthermore this fork also supports Markdown Tables.

# Include via Gradle

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Then, add the library to your project `build.gradle`
```gradle
dependencies {
    compile 'com.github.budsmile:bypass-android:1.0.2'
}
```

This library is provided as a "fat" aar with native binaries for all available architectures. To
reduce your APK size, use the ABI filtering/splitting techniques in the Android plugin:
http://tools.android.com/tech-docs/new-build-system/user-guide/apk-splits

# Usage
If you just want to have your Markdown parsed check out the [Simple Usage Guide](#simple-usage).
If you want to customize the Output either check out the [Bypass.Options Guide](#bypassoptions)
or the [SpanProvider Guide](#extending-spanprovider).

##Simple Usage
If you want to use Bypass without customizing anything all you have to do
is the following:

```java
TextView sampleText = (TextView) findViewById(R.id.sample_text);
String markdown = "*[Test](https://www.google.com)*";

// The this argument has to extend from
// Context (e.g. an Activity)
Bypass bypass = new Bypass(this);
CharSequence string = bypass.markdownToSpannable(markdown);
sampleText.setText(string);
sampleText.setMovementMethod(LinkMovementMethod.getInstance());
```

##Bypass.Options
*Small Customizations*

The original Bypass.Options already allowed for some customization but this extended
Version allows for a lot more customizations. Some examples are provided here. For more
examples check out the Source Code of Bypass.Options.

###Appending Authority to Autolinks
```java
//Default is true
setAppendAuthorityToTextLink(boolean append);
```

Setting this to `true` will append the Authority of an Autolink to it. E.g.:

```markdown
[That Search Engine](https://www.google.com)
```

will be converted to

[That Search Engine - (www.google.com)](https://www.google.com)


##Extending SpanProvider
*Full Customization*

If you want to use your own Spans for some Elements you'll have to extend DefaultSpanProvider. E.g.:

```java
class CustomSpanProvider extends DefaultSpanProvider {
    public CustomSpanProvider(Bypass.Options bypassOptions) {
        super(bypassOptions);
    }

    @Override
    public Object[] onCreateLinkSpans(String url) {
        return new Object[] {
                new ClickableUrlSpan(url),
                new StyleSpan(Typeface.ITALIC)
        };
    }
}
```

Now, when instantiating Bypass just do the following:

```java
Bypass.Options options = new Bypass.Options();
Bypass bypass = new Bypass(this, options, new CustomSpanProvider(options));
```

That's it! :) Your CustomSpanProvider will now create all Links in Italic. And there are a lot more methods to override. Just check out the Source Code to understand how the methods normally create their Spans.

#Tables
By default Markdown Tables will be replaced with a "View Table" Link that opens a Dialog
with the Table.

If you want to suppress this behaviour call

```java
setParseTables(false);
```

on your `Bypass.Options` Object.


# Robolectric
See [this issue](https://github.com/Commit451/bypasses/issues/2) for an explination for getting Robolectric to work.

# Proguard
This dependency also packages the Proguard rules [suggested](https://github.com/Uncodin/bypass/issues/195) for bypass to work properly with Proguard enabled

License
--------

    Copyright 2016 budsmile
    Copyright 2015 Commit 451
    Copyright 2015 Uncodin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

