# How to contribute to this project

## Table of Contents

- [Maintainers](#maintainers)
- [Reporting Issues](#reporting-issues)
- [Feature Requests](#feature-requests)
- [Submitting Changes](#submitting-changes)
- [Issue and PR Lifecycle](#issue-and-pr-lifecycle)
- [Contributing to these guidelines](#contributing-to-these-guidelines)

## Maintainers

- Hari Krishna [@hari.krishna1](https://github.com/HariKrishna1-ibm)
- Lokanadham Nalla [@lokanadham.nalla1](https://github.com/lokanalla)
- Mahesh Pulle [@mahesh.pulle](https://github.ibm.com/Mahesh-Pulle)

## Reporting Issues

Firstly, consider the following:

- Ensure you are using the latest version or any version you believe is considered compatible with your current version of CP4NA

- Check the existing list of [issues](https://github.com/IBM/velocloud-sdwan-driver/issues) to see if the bug you've found has already been reported.

When reporting a bug, please include:

- A description of the expected behaviour
- A description of the actual behaviour
- The steps to reproduce the issue
- Full stack trace/log files where possible
- A description of your environment:
    - If running locally, include: Java version, Operating System, CP4NA version
    - If running on OCP/Kubernetes, include: OCP/Kubernetes version, Docker image version, CP4NA version
- The `bug` label

## Feature Requests

Firstly, check the existing list of [issues](https://github.com/IBM/velocloud-sdwan-driver/issues) to see if the feature has already been requested. Feel free to upvote the issue to add your support but only add comments if you intend to add to the idea, give feedback or provide additional use cases/reasons why the feature is valuable.

When requesting a feature, please include:

- A detailed description of the proposed functionality
- A description of the value or intended use case for the feature
- If possible, a rough description of the changes you believe this would require (either high level or low level code is ok)
- The `enhancement` label

## Submitting Changes

Before submitting changes, please complete the following:

- Ensure there is an open issue for your intended change. If not, raise one **before** starting work, as this allows other users/maintainers to give feedback on the suggested idea before you make changes.

- Assign yourself to the issue so others are aware it's being worked on

When working on the changes, please complete the following:

- Create a branch for your work based off the `develop` branch (**not** `master`). The `develop` branch is used for ongoing development and is merged to `master` when releases are finalised. 

- Name your branch `issue/<issue number>` where `issue number` corresponds to the unique ID of the issue e.g. work on [issue#115](https://github.com/IBM/velocloud-sdwan-driver/issues/115) was completed on branch [issue/115](https://github.com/IBM/velocloud-sdwan-driver/pull/116)

- Include relevant tests along with your code changes. 

 When submitting a pull request, please complete the following:

- Create the [pull request on Github](https://github.com/IBM/velocloud-sdwan-driver/compare) with `base: develop` and `compare: your-branch`. 

- In the description of your PR add `Fixes #issue-number` e.g. `Fixes #115` 

- Assign the PR to the maintainers of this project (see start of this document)

- Monitor the PR for comments/approval, responding to any suggested changes. Once at least one maintainer has approved the PR it will be merged when deemed appropriate (usually on approval but there may be rare cases where changes are put on hold).

## Issue and PR Lifecycle

Maintainers will manage the lifecycle of all issues and PRs, this includes:

- assigning/adjusting milestones
- assigning/adjusting tags
- closing issues
- merging/closing PRs

## Contributing to these guidelines

There's still a lot missing from these guidelines, such as:

- Code Style guidelines (or even better, a pre-commit hook to automatically format code)
- Mailing list/forum to ask questions

If you have any ideas to improve these guidelines, then follow the same process as a feature enhancement by raising an issue and proposing your suggestions. These can be discussed and then, where applicable, changes can be made and a PR raised (using the same process as for code changes).
