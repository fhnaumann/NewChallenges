---
name: implement_rule
about: Implement a new rule.
title: Implement [Rule]
labels: builder-website, docs, plugin
assignees: ''

---

- [ ] Add model classes for [Rule] in Typescript files.
- [ ] Create subpage on builder website with Vue Component.
- [ ] Add connection in Vue Router `index.ts`.
- [ ] Create Java classes in plugin.
  - [ ] Create [Rule] class.
  - [ ] Add if check in `ModelMapper`.
  - [ ] Create [Rule]Validator class.
  - [ ] Add new Validatior class to existing validators.
  - [ ] Write unit for [Rule] and [Rule]Validator class.
- [ ] Write cypress e2e test
  - [ ] Create new `[Rule]-test.cy.test` test case
  - [ ] Add `configure[Rule]` method signurate to `e2e.ts` and implementation of the method to `commands.ts`.
- [ ] Write documentation by creating a new file in retype.
  - [ ] Explain the variables in [Rule].
  - [ ] Use the written e2e tests as example data.
