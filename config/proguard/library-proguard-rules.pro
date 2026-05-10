# Keep public and protected library entry points stable while allowing R8 to shrink
# private and internal implementation details.
-keep class com.b231001.bmaterial.** {
    public protected *;
}
