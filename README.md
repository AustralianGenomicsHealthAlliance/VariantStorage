# VariantStorage

## Webservice API

To authenticate using the curl command:

    curl -XPOST -H 'Content-Type:json' --data '{"username":"admin", "password":"agha123"}' http://localhost:8080/api/login

As a response from the authentication you will get some tokens in the header. The important token is the 'access_token':

    {"username":"admin","roles":["ROLE_ADMIN"],"token_type":"Bearer","access_token":"eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiJINHNJQUFBQUFBQUFBSlZTUDBcL2JRQlJcL1RoTlJnVlNnRWtnZFlBRTI1RWgwek1UZnFwVWJVTk1zSUlFdTlzTTlPTitadXpNa1M1V0pEaGxTMFNKVjdWZmdtN1JMUHdCcWg2N01YZnZPRUJ4WVVHK3kzXC8zOCtcL2Q4Y1FVVm8rRjVyQmtYeGs5RkZuUHBtMVJ6R1JzTU04MXR4ODhNNmdodGpuaVJBNXMwZ2V2amxjQUxvTVFqQzArREEzYk1xb0xKdUxyWk9zRFExdG9hbHBTT2J4ajNOVXZ3Uk9sRFwvNVk3VkJydkNCVFUzdGNTakd6REpBdERsVWxiVjNLOW5YS04wVFpNRkxOQWhZZHVOQlhTRFVyTG1UREQwQkdVckNVd0NtQ01aZmFkSWxXT3hzTDR0ZG5NY2xGdG9LMEY4RGhseHBDN2Uwa2ExbGwzOTg2bXBBUkg4QjdLN2RTalE5MHRPS2p2ZVB4VkpRU2w1a3FhK2FaTVZNVDN1Uk1uXC91N00yY1wvK3QyNnpCRUNkTEQ3OFRURlwvdGdMZDc3dFwvWlwvT2l2ZERDOUpEMUFsWnJwK1Jtc21CK3E5RXBYMzdaK25SKzlXSG5FU2s3eE1iXC83Mk4rK2FhNXpxcEtVcWFaVlVNN0l0cVRzbnNtOHBXSHlRZGI2UGdObnFRQzZZK1NGcU5iaVlLWTRwYTFFb08rTFl5KzJRelc5NWJYWHIrc3U5Y0tpeEl1U2ZWSkh0cHR5dzhVN2FyMzUrT1BcL3R3dlluZ0ZsV01tTXFUT0p3cFFQVXRhcUU4dnptZkdQdlwvdTVRa0dmXC9NXC9DakNzM1JFREFBQT0iLCJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiZXhwIjoxNDg0NTI5NzYyLCJpYXQiOjE0ODQ1MjYxNjJ9.C1I_MnkLDlNthMPw9EYtqQakb9YqEAx1LrQ1078Bk_k","expires_in":3600,"refresh_token":"eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiJINHNJQUFBQUFBQUFBSlZTUDBcL2JRQlJcL1RoTlJnVlNnRWtnZFlBRTI1RWgwek1UZnFwVWJVTk1zSUlFdTlzTTlPTitadXpNa1M1V0pEaGxTMFNKVjdWZmdtN1JMUHdCcWg2N01YZnZPRUJ4WVVHK3kzXC8zOCtcL2Q4Y1FVVm8rRjVyQmtYeGs5RkZuUHBtMVJ6R1JzTU04MXR4ODhNNmdodGpuaVJBNXMwZ2V2amxjQUxvTVFqQzArREEzYk1xb0xKdUxyWk9zRFExdG9hbHBTT2J4ajNOVXZ3Uk9sRFwvNVk3VkJydkNCVFUzdGNTakd6REpBdERsVWxiVjNLOW5YS04wVFpNRkxOQWhZZHVOQlhTRFVyTG1UREQwQkdVckNVd0NtQ01aZmFkSWxXT3hzTDR0ZG5NY2xGdG9LMEY4RGhseHBDN2Uwa2ExbGwzOTg2bXBBUkg4QjdLN2RTalE5MHRPS2p2ZVB4VkpRU2w1a3FhK2FaTVZNVDN1Uk1uXC91N00yY1wvK3QyNnpCRUNkTEQ3OFRURlwvdGdMZDc3dFwvWlwvT2l2ZERDOUpEMUFsWnJwK1Jtc21CK3E5RXBYMzdaK25SKzlXSG5FU2s3eE1iXC83Mk4rK2FhNXpxcEtVcWFaVlVNN0l0cVRzbnNtOHBXSHlRZGI2UGdObnFRQzZZK1NGcU5iaVlLWTRwYTFFb08rTFl5KzJRelc5NWJYWHIrc3U5Y0tpeEl1U2ZWSkh0cHR5dzhVN2FyMzUrT1BcL3R3dlluZ0ZsV01tTXFUT0p3cFFQVXRhcUU4dnptZkdQdlwvdTVRa0dmXC9NXC9DakNzM1JFREFBQT0iLCJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNDg0NTI2MTYyfQ.ZEGRC3YjqQFiSerydyPBRZWnNC36aUQ7-e2uVcPDWuo"}


To invoke a web service, supply the 'access_token' in the header as follows:

    curl -v -XGET -H "Authorization: Bearer access_token_here" http://localhost:8080/dataset/search?name=cohortName

If the token was successful, then you should get a status code of 200:

    < HTTP/1.1 200
    < X-Application-Context: application:development
    < Set-Cookie: JSESSIONID=3A4144A2B8F3527A1B98270DAB821C2B;path=/;HttpOnly
    < Content-Type: application/json;charset=utf-8
    < Transfer-Encoding: chunked
    < Date: Mon, 16 Jan 2017 00:19:19 GMT

Otherwise, you may get access denied with status code 401:

    < HTTP/1.1 401
    < WWW-Authenticate: Bearer error="invalid_token"
    < Content-Length: 0
    < Date: Mon, 16 Jan 2017 00:19:37 GMT

