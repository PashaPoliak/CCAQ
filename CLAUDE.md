# Project: Claude Certified Architect

## Tech Stack
Language: TypeScript / Node.js 18
Framework: NestJS
Database: PostgreSQL via TypeORM

## Coding Standards
- Use async/await, never raw Promises
- All functions must have JSDoc comments
- Max file length: 300 lines
- No console.log in production code

## Allowed Commands
npm test, npm run lint, npm run build, git status, git diff

## Restricted Commands
Never run: git push, npm publish, rm -rf, kubectl delete, terraform destroy

## Testing
Run tests with: npm test
Always run tests before marking a task complete.
