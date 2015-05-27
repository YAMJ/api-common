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

Project Documentation
---------------------
The automatically generated documentation can be found [HERE](http://yamj.github.io/api-common/)
