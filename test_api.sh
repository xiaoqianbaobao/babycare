#!/bin/bash

# Test script for BabyCare API
BASE_URL="http://localhost:8080/api"

echo "Testing BabyCare API"

# Login with provided credentials
echo "Logging in with newuser789/test123456"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser789","password":"test123456"}')

echo "Login response: $LOGIN_RESPONSE"

# Extract token
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')

if [ "$TOKEN" != "null" ]; then
  echo "Login successful, token: $TOKEN"
  
  # Get user's families
  echo "Getting user families..."
  FAMILIES_RESPONSE=$(curl -s -X GET "$BASE_URL/family/my" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json")
  
  echo "Families response: $FAMILIES_RESPONSE"
  
  # If we have families, get babies
  FAMILY_ID=$(echo $FAMILIES_RESPONSE | jq -r '.data[0].id')
  
  if [ "$FAMILY_ID" != "null" ]; then
    echo "Getting babies for family $FAMILY_ID..."
    BABIES_RESPONSE=$(curl -s -X GET "$BASE_URL/family/$FAMILY_ID/babies" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json")
    
    echo "Babies response: $BABIES_RESPONSE"
    
    # If we have babies, try to create a growth record
    BABY_ID=$(echo $BABIES_RESPONSE | jq -r '.data[0].id')
    
    if [ "$BABY_ID" != "null" ]; then
      echo "Creating growth record for baby $BABY_ID..."
      RECORD_RESPONSE=$(curl -s -X POST "$BASE_URL/growth-record/create" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"babyId\":$BABY_ID,\"type\":\"DIARY\",\"title\":\"Test Record\",\"content\":\"This is a test record\"}")
      
      echo "Record creation response: $RECORD_RESPONSE"
    else
      echo "No babies found"
    fi
  else
    echo "No families found"
  fi
else
  echo "Login failed"
fi