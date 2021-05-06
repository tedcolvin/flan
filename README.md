# Treexl - Tree extensible expression language

## Introduction

Treexl is a general purpose expression language designed for embedding in applications or frameworks
that need a simple language define custom rules without recompilation.

## Features

- inspired by the expression subset of SQL (the 'where part'), being easy to learn
- parse to expression trees, not to a direct executable format, so it can be:
  - validated
  - transformed
  - analyzed
  
## Use cases

### Filter language

Then main use case for Treexl is to be used to filter APIs in a portable, secure, efficient and flexible way.

Consider a REST or GraphQL API that return a large collection of elements that need some sort filtering.
Normaly that would need some additional parameters to control the filteria criteria/condition. The
problem is that leads to some sort of custom "ad-hoc", half-baked solution that is difficult to implement
and not powerfull enough.

With Treexl is possible to support a large (safe) subset o 'SQL where', including the ability to generate
the actual 'SQL where' expression that is sent to the database, without gotchas like SQL Injection but without
compromising the performance.

## License

Apache 2.0



