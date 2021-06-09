## semanticdb-for-scala3
![CI](https://github.com/tanishiking/semanticdb-for-scala3/actions/workflows/ci.yml/badge.svg)

The project that generates Scala codes from semanticdb.proto for Scala3 compiler. Context: https://github.com/scalameta/scalameta/issues/2367

- Generate Scala code from `semanticdb.proto` using Scalapb
- Adjust the generated codes for Scala3
  - Remove `scalapb-runtime` dependencies so Scala3 doesn't need to depend on `scalapb-runtime`.
  - Replace annotations `@transient` to `@sharable`, remove `SerialVersionUID`
  - derive `CanEqual` for generaged class and traits.

### Usage
- `clean-generated`: delete generated files.
- `generate`: generate Scala files and adjust them for Scala3 compiler.

### Workflow
```bash
$ sbt
> clean
> clean-generated
> generate
```
