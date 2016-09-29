# ieee754lib
Java library for encoding/decoding floating point numbers to/from IEEE 754 bit strings

Encoding, decoding, and conversion of all basic interchange formats is supported:

| Name        | Common name | Exponent bits | Significand bits | Exponent bias |
|:----------- |:----------- | -------------:| ----------------:| -------------:|
| [binary16]  | Half        |             5 |               10 |            15 |
| [binary32]  | Single      |             8 |               23 |           127 |
| [binary64]  | Double      |            11 |               52 |          1023 |
| [binary128] | Quadruple   |            15 |              112 |         16383 |
| [binary256] | Octuple     |            19 |              236 |        262143 |

Arbitrary formats are also supported by providing custom values for:
* Exponent bits
* Significand bits
* Exponent bias

Latest release: [ieee754lib-1.0.1.jar](https://github.com/Kerbaya/ieee754lib/releases/download/v1.0.1/ieee754lib-1.0.1.jar)

[binary16]: https://en.wikipedia.org/wiki/Half-precision_floating-point_format
[binary32]: https://en.wikipedia.org/wiki/Single-precision_floating-point_format
[binary64]: https://en.wikipedia.org/wiki/Double-precision_floating-point_format
[binary128]: https://en.wikipedia.org/wiki/Quadruple-precision_floating-point_format
[binary256]: https://en.wikipedia.org/wiki/Octuple-precision_floating-point_format
