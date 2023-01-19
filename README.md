# RESTfulHTTPServer
RESTful HTTP Server in Java

Usage:
	Call api with form ip:port/methodName?param1=value1&param2=value2


Limitations:
	Will not work with multiple methods with the same name (different param signatures)
	Server parameters must be primitive or class-type primitives e.g. int and Integer both work
	Return type must be anything that can be converted to a string using .toString
	