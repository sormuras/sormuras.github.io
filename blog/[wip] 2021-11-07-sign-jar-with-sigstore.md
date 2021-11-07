## Sign JAR file, keyless with https://sigstore.dev

Download `junit-4.13.2.jar` from <https://repo.maven.apache.org/maven2/junit/junit/4.13.2/>.

### Sign

```text
> set COSIGN_EXPERIMENTAL=1
> cosign sign-blob junit-4.13.2.jar

Using payload from: junit-4.13.2.jar
Generating ephemeral keys...
Retrieving signed certificate...
Non-interactive mode detected, using device flow.
Enter the verification code DJCC-GXPW in your browser at: https://oauth2.sigstore.dev/auth/device?user_code=DJCC-GXPW
Code will be valid for 300 seconds
Token received!
Successfully verified SCT...
signing with ephemeral certificate:
-----BEGIN CERTIFICATE-----
MIICpDCCAiugAwIBAgIUAPEbKPoFGLcoBCDYnmLwVai0XZ4wCgYIKoZIzj0EAwMw
KjEVMBMGA1UEChMMc2lnc3RvcmUuZGV2MREwDwYDVQQDEwhzaWdzdG9yZTAeFw0y
MTExMDcxNjU3NThaFw0yMTExMDcxNzE3NTdaMAAwWTATBgcqhkjOPQIBBggqhkjO
PQMBBwNCAATS6N/S6S434Ff8iUCYu1O4G4U9adzN0//9GeR32YFRqKUYyeoDNZF2
5w40idE3IPrpubq718YP8UvGUDCO0IZVo4IBVzCCAVMwDgYDVR0PAQH/BAQDAgeA
MBMGA1UdJQQMMAoGCCsGAQUFBwMDMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFOE5
hUddPi3juO6VGnxCW6xX44ssMB8GA1UdIwQYMBaAFMjFHQBBmiQpMlEk6w2uSu1K
BtPsMIGNBggrBgEFBQcBAQSBgDB+MHwGCCsGAQUFBzAChnBodHRwOi8vcHJpdmF0
ZWNhLWNvbnRlbnQtNjAzZmU3ZTctMDAwMC0yMjI3LWJmNzUtZjRmNWU4MGQyOTU0
LnN0b3JhZ2UuZ29vZ2xlYXBpcy5jb20vY2EzNmExZTk2MjQyYjlmY2IxNDYvY2Eu
Y3J0MCAGA1UdEQEB/wQWMBSBEnNvcm11cmFzQGdtYWlsLmNvbTAsBgorBgEEAYO/
MAEBBB5odHRwczovL2dpdGh1Yi5jb20vbG9naW4vb2F1dGgwCgYIKoZIzj0EAwMD
ZwAwZAIwCtSeLE0F8pK3MHmtzVrO+i0wfxDAgdww6JU/5kBsWpwZqPjegCJYAxv4
uLYJ1vVuAjBzk18Hqej/NuJ8Z3iKmxDHYgdaex+KdQBOKOvtEFRGaZjFPmWqu1zB
rp+tH6kgb5I=
-----END CERTIFICATE-----

tlog entry created with index: 833640
MEUCIHtHWafADrgpzW4Pj4jjfaOXuvQT35TK9LlRV6XZ+KdqAiEAquFaYjOV8Wockyqh/EWdJgTB4x7PDyLSTk3zEofVjXM=
```

### Verify

```text

> certutil -hashfile junit-4.13.2.jar SHA256

8e495b634469d64fb8acfa3495a065cbacc8a0fff55ce1e31007be4c16dc57d3
```

```text
> rekor search --sha 8e495b634469d64fb8acfa3495a065cbacc8a0fff55ce1e31007be4c16dc57d3

Found matching entries (listed by UUID):
66a0d3f8d4164e798d11a79f2455f5b41dcc067fe7b4f5a02212ad7ae9e93d65
```

```text
> rekor get --uuid 66a0d3f8d4164e798d11a79f2455f5b41dcc067fe7b4f5a02212ad7ae9e93d65

LogID: c0d23d6ad406973f9559f3ba2d1ca01f84147d8ffc5b8445c224f98b9591801d
Index: 833640
IntegratedTime: 2021-11-07T16:57:59Z
UUID: 66a0d3f8d4164e798d11a79f2455f5b41dcc067fe7b4f5a02212ad7ae9e93d65
Body: {
  "RekordObj": {
    "data": {
      "hash": {
        "algorithm": "sha256",
        "value": "8e495b634469d64fb8acfa3495a065cbacc8a0fff55ce1e31007be4c16dc57d3"
      }
    },
    "signature": {
      "content": "MEUCIHtHWafADrgpzW4Pj4jjfaOXuvQT35TK9LlRV6XZ+KdqAiEAquFaYjOV8Wockyqh/EWdJgTB4x7PDyLSTk3zEofVjXM=",
      "format": "x509",
      "publicKey": {
        "content": "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUNwRENDQWl1Z0F3SUJBZ0lVQVBFYktQb0ZHTGNvQkNEWW5tTHdWYWkwWFo0d0NnWUlLb1pJemowRUF3TXcKS2pFVk1CTUdBMVVFQ2hNTWMybG5jM1J2Y21VdVpHVjJNUkV3RHdZRFZRUURFd2h6YVdkemRHOXlaVEFlRncweQpNVEV4TURjeE5qVTNOVGhhRncweU1URXhNRGN4TnpFM05UZGFNQUF3V1RBVEJnY3Foa2pPUFFJQkJnZ3Foa2pPClBRTUJCd05DQUFUUzZOL1M2UzQzNEZmOGlVQ1l1MU80RzRVOWFkek4wLy85R2VSMzJZRlJxS1VZeWVvRE5aRjIKNXc0MGlkRTNJUHJwdWJxNzE4WVA4VXZHVURDTzBJWlZvNElCVnpDQ0FWTXdEZ1lEVlIwUEFRSC9CQVFEQWdlQQpNQk1HQTFVZEpRUU1NQW9HQ0NzR0FRVUZCd01ETUF3R0ExVWRFd0VCL3dRQ01BQXdIUVlEVlIwT0JCWUVGT0U1CmhVZGRQaTNqdU82VkdueENXNnhYNDRzc01COEdBMVVkSXdRWU1CYUFGTWpGSFFCQm1pUXBNbEVrNncydVN1MUsKQnRQc01JR05CZ2dyQmdFRkJRY0JBUVNCZ0RCK01Id0dDQ3NHQVFVRkJ6QUNobkJvZEhSd09pOHZjSEpwZG1GMApaV05oTFdOdmJuUmxiblF0TmpBelptVTNaVGN0TURBd01DMHlNakkzTFdKbU56VXRaalJtTldVNE1HUXlPVFUwCkxuTjBiM0poWjJVdVoyOXZaMnhsWVhCcGN5NWpiMjB2WTJFek5tRXhaVGsyTWpReVlqbG1ZMkl4TkRZdlkyRXUKWTNKME1DQUdBMVVkRVFFQi93UVdNQlNCRW5OdmNtMTFjbUZ6UUdkdFlXbHNMbU52YlRBc0Jnb3JCZ0VFQVlPLwpNQUVCQkI1b2RIUndjem92TDJkcGRHaDFZaTVqYjIwdmJHOW5hVzR2YjJGMWRHZ3dDZ1lJS29aSXpqMEVBd01EClp3QXdaQUl3Q3RTZUxFMEY4cEszTUhtdHpWck8raTB3ZnhEQWdkd3c2SlUvNWtCc1dwd1pxUGplZ0NKWUF4djQKdUxZSjF2VnVBakJ6azE4SHFlai9OdUo4WjNpS214REhZZ2RhZXgrS2RRQk9LT3Z0RUZSR2FaakZQbVdxdTF6QgpycCt0SDZrZ2I1ST0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo="
      }
    }
  }
}
```

```text
> certutil -decode junit-4.13.2.cert.b64 junit-4.13.2.cert
```

```text
> cosign verify-blob -signature junit-4.13.2.sig -cert junit-4.13.2.cert junit-4.13.2.jar

No TUF root installed, using embedded CA certificate.
Certificate is trusted by Fulcio Root CA
Email: [sormuras@gmail.com]
Verified OK
tlog entry verified with uuid: "66a0d3f8d4164e798d11a79f2455f5b41dcc067fe7b4f5a02212ad7ae9e93d65" index: 833640
```
