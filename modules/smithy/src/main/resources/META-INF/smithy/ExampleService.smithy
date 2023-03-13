$version: "2"
namespace smithy4s.hello

use alloy#simpleRestJson

@simpleRestJson
service HelloWorldService {
    version: "1.0.0",
    operations: [Hello]
}

@http(method: "POST", uri: "/{name}", code: 200)
operation Hello {
    input: Person,
    output: Greeting
}

structure Person with [Tracing] {
    @httpLabel
    @required
    name: String,

    @httpQuery("town")
    town: String,
}

@trait(selector: "structure > member")
structure tracingHeader {}

@mixin
structure Tracing {
    @httpHeader("requestId")
    @tracingHeader
    requestId: String

    @httpHeader("traceId")
    @tracingHeader
    traceId: String
}

structure Greeting {
    @required
    message: String
}