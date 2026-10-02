# Module dialogplus

DialogPlus displays list, grid and custom view dialogs inside an Activity's content view.
Start with [com.orhanobut.dialogplus.DialogPlus.newDialog], configure a
[com.orhanobut.dialogplus.DialogPlusBuilder], then call `create().show()` on the UI thread.

[ListHolder][com.orhanobut.dialogplus.ListHolder] and
[GridHolder][com.orhanobut.dialogplus.GridHolder] accept Android `BaseAdapter` instances.
[ViewHolder][com.orhanobut.dialogplus.ViewHolder] accepts a layout resource or a View.
Dimensions supplied to the builder are pixels, except `MATCH_PARENT` and `WRAP_CONTENT`.
