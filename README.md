# azure-functions-java-shortcode
A small application that can generate / persist / lookup short codes for a given URL.

Provides two functions that can be called:

1. shortcode

Accepted as a POST call, expects a `url` query string representing the URL to be turned into a shortcode. Returns status code 200 when successful, with the body being the full short url, e.g. http://jogil.es/20490

2. redirect

Accepted as a GET call, expects a `name` query string representing the short code to be turned into the (previously stored) long url. Returns status code 302 with a `Location` response header pointing to the long url.
