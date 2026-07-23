# SkillPulse-Developer-Skill-Assessment-Platform
SkillPulse — Developer Skill Assessment Platform

**What it is**: A platform where developers register, take timed skill assessments (like coding quizzes), get scored, earn badges, and track their skill growth over time. Companies can create assessments and view leaderboards of top performers.

#### The story of the system:
A developer signs up → takes a timed assessment → 
submits answers → gets scored → earns badges →
appears on leaderboard → company views top performers

## SYSTEM ARCHITECTURE

                    ┌────────────────────────────-─┐
                    │      React Frontend          │
                    │   (RTK Query + Redux)        │
                    └──────────────┬──────────────-┘
                                   │ REST
                    ┌──────────────▼──────────────-┐
                    │     API Gateway Concept      │
                    │  (just port routing for now) │
                    └────┬─────────────┬───────────┘
                         │             │
           ┌─────────────▼──┐    ┌─────▼────────────-──┐
           │  Assessment    │    │   User Service      │
           │  Service       │    │   (port 8082)       │
           │  (port 8081)   │    │                     │
           └────────┬───────┘    └──────────┬──────────┘
                    │                       │
           ┌────────▼───────┐    ┌──────────▼──────────┐
           │ assessment_db  │    │    user_db          │
           └────────────────┘    └─────────────────────┘
                    │
                    │ Kafka Events
                    ▼
           ┌─────────────────┐
           │  Score Service  │
           │  (port 8083)    │
           └────────┬────────┘
                    │
           ┌────────▼───────┐
           │   score_db     │
           └────────────────┘