# Smart Appointment Booking & Allocation System - Problem Statement

## 🎯 Project Overview
Build an intelligent appointment booking system for retail stores (banks, salons, car service centers) that automatically allocates appointments to available associates based on skills, availability, load balancing, and business rules.

**Example Use Cases:**
- Bank branch: Customer books appointment for loan consultation → Allocated to loan specialist
- Car Service Center: Customer books appointment for AC repair → Allocated to AC technician
- Salon: Customer books haircut + coloring → Allocated to stylist with both skills

---

## 📋 Business Context

### The Problem
**RetailHub** operates 50+ retail locations (banks, service centers, salons) with 500+ associates. Currently:
- ❌ Manual appointment allocation takes 2-3 hours daily
- ❌ Associates are overbooked or sitting idle
- ❌ No-shows waste 20% of time slots
- ❌ Wrong skill allocation leads to poor customer experience
- ❌ Peak hour rush causes long wait times

### The Solution
Build an automated system that:
- ✅ Customers can book appointments online
- ✅ Automatically allocates to best-fit associate
- ✅ Handles real-time availability
- ✅ Manages no-shows and cancellations
- ✅ Provides analytics for optimization

---

## 🎪 Core Functional Requirements

### 1. Customer Appointment Booking

**User Stories:**
- As a customer, I want to select service type (loan, car repair, haircut, etc.)
- As a customer, I want to choose preferred date and time slot
- As a customer, I want to select a specific location/branch
- As a customer, I want to see available time slots
- As a customer, I want to receive booking confirmation with associate details
- As a customer, I want to reschedule or cancel my appointment
- As a customer, I want to specify special requirements/notes

**Booking Flow:**
```
1. Customer selects service type
2. System shows available locations
3. Customer selects location
4. System shows available dates
5. Customer selects date
6. System shows available time slots (based on associate availability)
7. Customer selects time slot
8. Customer provides personal details
9. System allocates to best associate
10. Booking confirmation sent
```

**Business Rules:**
- Booking can be made 1 hour to 30 days in advance
- Minimum 15-minute slots
- Maximum 3 active bookings per customer
- Cancellation allowed up to 2 hours before appointment
- Rescheduling allowed up to 4 hours before appointment

---

### 2. Associate Allocation Engine (CORE FEATURE)

**Allocation Requirements:**
This is the heart of the system. The allocation algorithm must consider:

#### A. Hard Constraints (Must satisfy)
1. **Skill Matching:** Associate must have required skills
2. **Availability:** Associate must be available at requested time
3. **Capacity:** Associate shouldn't exceed maximum appointments per day
4. **Working Hours:** Must be within associate's shift timings
5. **Leave/Break:** Associate shouldn't be on leave or break

#### B. Soft Constraints (Optimization criteria)
1. **Load Balancing:** Distribute appointments evenly across associates
2. **Customer Preference:** Prefer previously served associate if requested
3. **Experience Level:** Match complex services to senior associates
4. **Geographic Proximity:** Prefer associates in same zone for multi-service
5. **Service Continuity:** Same associate for follow-up appointments
6. **Customer Rating:** Prefer higher-rated associates

#### C. Allocation Algorithm (Priority Order)

```
STEP 1: Filter Eligible Associates
- Has required skills ✓
- Available at requested time ✓
- Not on leave/break ✓
- Within working hours ✓
- Has capacity ✓

STEP 2: Calculate Score for Each Eligible Associate
Score = (Skill_Match_Score × 40%) 
      + (Load_Balance_Score × 25%)
      + (Customer_Preference_Score × 20%)
      + (Experience_Score × 10%)
      + (Rating_Score × 5%)

STEP 3: Select Top Scored Associate

STEP 4: Reserve Time Slot (Lock for 10 minutes)

STEP 5: If booking fails, release lock and retry
```

---

### 3. Edge Cases & Constraint Handling

#### Edge Case 1: No Available Associate
**Scenario:** Customer requests appointment but no associate available

**Solution Flow:**
```
1. Check if any associate can be freed up
   - Can we reschedule lower priority appointments?
   - Can we assign to associate in nearby location?
   
2. If not possible:
   - Suggest alternative time slots (next 3 available)
   - Suggest alternative locations within 5km
   - Allow waitlist booking (notify if slot opens)
   
3. Store as "Unallocated Appointment" with status "PENDING"
4. Trigger notification to manager for manual intervention
```

---

#### Edge Case 2: Overbooking Situation
**Scenario:** Multiple customers book simultaneously for same slot
