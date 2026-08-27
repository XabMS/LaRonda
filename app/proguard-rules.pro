# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line number information for debugging production stack traces.
# Upload the generated mapping file (app/build/outputs/mapping/release/mapping.txt)
# to the Play Console after each release.
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name while keeping line numbers.
-renamesourcefileattribute SourceFile
