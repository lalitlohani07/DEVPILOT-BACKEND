# DevPilot — Known Limitations / Future Improvements (Phase 1)

1. **No transaction management.** Service methods that perform multiple
   repository writes (e.g. reportIncident: save Incident + save IncidentEvent)
   are NOT atomic. If the second write fails, the first is not rolled back.
   Fix: wrap with @Transactional once Spring transaction management is learned.

2. **N+1 query problem in IncidentRepository.findAll() / IncidentEventRepository
   row mapping.** Each row triggers 1-2 additional queries via userRepository/
   incidentRepository.findById(). Fine at small scale, will not scale.
   Fix: rewrite with SQL JOINs once comfortable with multi-table result mapping.

3. **setId()/setCreatedAt()/setUpdatedAt() are public but repository-only
   by convention, not by language enforcement.** Nothing stops service code
   from misusing them today. Fix: revisit once Spring Data JPA is learned —
   this entire problem disappears under JPA's entity lifecycle management.

4. **CLOSED incidents can be reopened directly to INVESTIGATING.** This was
   a deliberate design choice (see ALLOWED_TRANSITIONS), not a bug — flagged
   here in case product requirements change.