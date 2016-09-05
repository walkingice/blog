title: Upgrade Retrofit 1.X to 2
s: retrofit
date: 2016-09-05 23:51:48
tags:
    - java
    - geek
    - android
categories: geek
---

I guess most of people alreay read the post [Retrofit 2 — Upgrade Guide from 1.9](https://futurestud.io/blog/retrofit-2-upgrade-guide-from-1-9), me too. It listed some points for upgrading. If you used Retrofit 1.x in production, you might get more problem than the guide in upgrading, at least I did. Just make a memo here for myself and other poor guy.

I had problems such as

* Primitive String was wrapped by double quotes
* Upload multipart-form by using RequestBody doesn't work

<!-- more -->

Weeks ago Twitter SDK [upgraded to v2.0](https://docs.fabric.io/android/changelog.html#id88) and its Retrofit dependency also migrate to 2.0. Congratulations! Since we are geek, using latest version is kind of curse in our gene, of course we want to use it.

# Double quotes for primitive string

Reference: [#1210 2.0-beta2 adding extra quotes to multipart string form values](https://github.com/square/retrofit/issues/1210) - a stackoverflow-liked issue

We usually define an API call interface like this

```java
// use Part annotation
@POST("/v1/new_feed")
Observable<MyResponse> postSomething(@NonNull @Part("title") String title);
```

if we call <code>postSomething("hello")</code>, we expect the post body should be

```
title=hello
```

Actually it will be

```
title="hello"
```

As the issue tracker mentioned, it due to the Gson converter regards the value as JSON object and convert it to String. Interesting thing is, sigod mentioned([#763](https://github.com/square/retrofit/issues/763#issuecomment-151262201)) this int primitive is a valid JSON

```
244
```

> Early versions of JSON (such as specified by RFC 4627) required that a valid JSON "document" must consist of only an object or an array type—though they could contain other types within them. This restriction was removed starting with RFC 7158, so that a JSON document may consist entirely of any possible JSON typed value.

No wonder Gson will regards the String primitive as a JSON. My way to deal with it is to create a ConverterFactory for String.

```java
public class PrimitiveStringConverterFactory extends Converter.Factory {
    static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] paramAnots,
                                                          Annotation[] methodAnots,
                                                          Retrofit retrofit) {
        if (String.class.equals(type)) {
            for (Annotation ant : paramAnots) {
                if (ant instanceof Part) {
                    return new Converter<String, RequestBody>() {
                        @Override
                        public RequestBody convert(String value) throws IOException {
                            return RequestBody.create(MEDIA_TYPE, value);
                        }
                    };
                }
            }
        }
        return null;
    }
}

.......

builder.addConverterFactory(new PrimitiveStringConverterFactory());
```

I would like to add a customized annotation for this Converter to distinguish should it convert the String or not, but Retrofit only accepts its [annotations](https://square.github.io/retrofit/2.x/retrofit/) so I quit.

# Upload images by multipart form

Reference: [#1063 How to Upload a File using Retrofit 2.0](https://github.com/square/retrofit/issues/1063)

One of the most change from Retrofit 2 is no more **TypedFile** (OkHttp3 drop it), instead to use RequestBody. It is not too hard to replace TypedFile by RequestBody by 'program'.

```java
Observable<MyResponse> doPost(
    @NonNull @Part("content") String comment,
    @Nullable @PartMap Map<String, RequestBody> images);

......

Map<String, RequestBody> images = new HasnMap<>();
images.put("images[0]", methodToCreateRequestBody(image1));
images.put("images[1]", methodToCreateRequestBody(image2));
doPost("life is struggle", images);
```

But I met a problem in run-time. As each comments mentioned in that issue, it seems the RequestBody doesn't work in Multipart-form uploading.

After reading the thread and give some try, this code works for me. I use [MultipartBody.Part](https://square.github.io/okhttp/3.x/okhttp/okhttp3/MultipartBody.Part.html) instead.

```java
Observable<MyResponse> doPost(
    @NonNull @Part("content") String comment,
    @Nullable @Part List<MultipartBody.Part> images);

......

List<MultiPartBody.Part> images = new ArrayList<>();
images.add(MultipartBody.Part.create(
        "images[0]", "img1.png", methodToCreateRequestBody(image1)));
images.add(MultipartBody.Part.create(
        "images[1]", "img2.png", methodToCreateRequestBody(image2)));

doPost("go ahead", images);
```

I am curious about the difference between two implementation. Use okhttp logger to inspect request body.

```
# Use Map

09-05 22:55:46.763 16172 20511 D OkHttp  : Content-Disposition: form-data; name="images[0]"
09-05 22:55:46.763 16172 20511 D OkHttp  : Content-Transfer-Encoding: binary
09-05 22:55:46.763 16172 20511 D OkHttp  : Content-Type: image/jpeg
09-05 22:55:46.763 16172 20511 D OkHttp  : Content-Length: 20738
09-05 22:55:46.763 16172 20511 D OkHttp  : .......(binary file content)
09-05 22:55:46.768 16172 20511 D OkHttp  : --8d0ff432-6d5a-4e16-bf62-ed613edcae96--
09-05 22:55:46.768 16172 20511 D OkHttp  : --> END POST (21356-byte body)

# Use List

09-05 23:05:08.618 22723 23479 D OkHttp  : --c9292ea2-2e9f-400e-81e0-ea148544a735
09-05 23:05:08.618 22723 23479 D OkHttp  : Content-Disposition: form-data; name="images[0]"; filename="bomberman.png"
09-05 23:05:08.618 22723 23479 D OkHttp  : Content-Type: multipart/form-data
09-05 23:05:08.618 22723 23479 D OkHttp  : Content-Length: 20738
09-05 22:55:46.763 16172 20511 D OkHttp  : .......(binary file content)
09-05 23:05:08.622 22723 23479 D OkHttp  : --c9292ea2-2e9f-400e-81e0-ea148544a735--

```

the field <code>filename</code> is the only one obvious difference I found.(of course as well as content-type). Probably, probably our server side implementation is too strict that regards <code>filename</code> as necessary field.

I tried read [RFC 2388](https://www.ietf.org/rfc/rfc2388.txt) and [RFC 1867](https://www.ietf.org/rfc/rfc1867.txt), it said

> File inputs may also identify the file name. The file name may be
> described using the 'filename' parameter of the "content-disposition"
> header. This is not required, but is strongly recommended in any case
> where the original filename is known. This is useful or necessary in
> many applications.

I think it is not necessary field. But maybe I am wrong in some place. Just keep studying.

