# Azure Functions & Java URL Shortener
A small Azure Function project that can generate / persist / lookup short codes for a given URL.

Provides two functions that can be called:

**shortcode**

Accepted as a POST call, expects a `url` query string representing the URL to be turned into a shortcode. Returns status code 200 when successful, with the body being the full short url, e.g. http://jogil.es/7a

An optional `shortcode` query string may also be supplied, to suggest a preferred shortcode.

**redirect**

Accepted as a GET call, expects a `shortcode` query string representing the short code to be turned into the (previously stored) long url. Returns status code 302 with a `Location` response header pointing to the long url.

## What are Azure Functions?
Azure Functions is the name for the 'serverless' offering on the Azure cloud service. There are APIs for many languages, but this test application makes use of the Java APIs. Here are some handy link:

 * [Introducing Azure Functions](https://azure.microsoft.com/en-us/blog/introducing-azure-functions/)
 * [Azure Functions Overview](https://azure.microsoft.com/en-us/services/functions/)
 * [Java Azure Functions Tutorial](https://docs.microsoft.com/en-us/azure/azure-functions/functions-create-first-java-maven)
 * [Java Azure GitHub repo](https://github.com/Azure/azure-functions-java-worker)
