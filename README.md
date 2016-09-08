Common API
==========

This is a set of common API functions used by the APIs of YAMJ

[![Build Status](http://jenkins.omertron.com/job/API-Common/badge/icon)](http://jenkins.omertron.com/job/API-Common)

##SimpleHttpClientBuilder
This class allows you to create a simple CloseableHttpClient for use in the APIs if required.

Firstly, create a SimpleHttpClientBuilder:

    SimpleHttpClientBuilder shcb = new SimpleHttpClientBuilder();
Then, set properties using the `.setXXX()` functions and finally create the httpClient that can be used by the APIs

    CloseableHttpClient httpClient = shcb.build();

Android Usage
-------------

If you want to use this library in an android project, then adding the following lines to your gradle file should help matters.

    useLibrary 'org.apache.http.legacy'
    compile group: 'org.apache.httpcomponents' , name: 'httpclient-android' , version: '4.3.5.1'


Project Documentation
---------------------
The automatically generated documentation can be found [HERE](http://yamj.github.io/api-common/)
