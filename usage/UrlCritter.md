# UrlCritter

The purpose of this ```Critter``` is to enable dynamic, runtime switching of URLs. It provides a very basic interface that allows saving of custom, runtime picked urls or choosing from a few, pre-programmed URLs.

Adding custom, prefilled URLS can be done via the following:
  1. ```TypedArray``` by calling ```addUrlTypedArray()```
  2. A ```String``` using ```addUrls()```
  3. A ```List``` of urls

To listen to URL changes:
**Note** this is a strong reference in static memory, so please ensure to call ```unregisterUrlChangeListener()``` properly.

```java

UrlCritter urlCritter = Debugger().getInstance().getCritter(UrlCritter.class, "myCritter");
urlCritter.registerUrlChangeListener(mChangeListener);


private final UrlCritter.UrlChangeListener mChangeListener = new UrlCritter.UrlChangeListener() {
        @Override
        public void onUrlChanged(String url) {
            // do something here
        }
    };

```
