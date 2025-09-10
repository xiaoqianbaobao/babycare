import requests
import json

# Base URL for the API
base_url = "http://localhost:8080/api"

# Test user credentials (you may need to adjust these)
username = "newuser789"
password = "test123456"

# First, let's try to register a user
register_data = {
    "username": username,
    "password": password,
    "email": "test@example.com",
    "nickname": "Test User"
}

print("Attempting to register user...")
try:
    register_response = requests.post(f"{base_url}/auth/register", json=register_data)
    print(f"Register response status: {register_response.status_code}")
    print(f"Register response body: {register_response.text}")
except Exception as e:
    print(f"Error during registration: {e}")

# Then, let's try to login
login_data = {
    "username": username,
    "password": password
}

print("\nAttempting to login...")
try:
    login_response = requests.post(f"{base_url}/auth/login", json=login_data)
    print(f"Login response status: {login_response.status_code}")
    print(f"Login response body: {login_response.text}")
    
    if login_response.status_code == 200:
        # Extract the token from the response
        login_result = login_response.json()
        token = login_result.get('data', {}).get('token')
        
        if token:
            print(f"Successfully logged in. Token: {token}")
            
            # Set up headers for authenticated requests
            headers = {
                "Authorization": f"Bearer {token}",
                "Content-Type": "application/json"
            }
            
            # First, create a family
            family_data = {
                "name": "Test Family",
                "description": "A test family for growth records"
            }
            
            print("\nAttempting to create family...")
            try:
                family_response = requests.post(f"{base_url}/family/create", json=family_data, headers=headers)
                print(f"Family creation response status: {family_response.status_code}")
                print(f"Family creation response body: {family_response.text}")
                
                if family_response.status_code == 200:
                    family_result = family_response.json()
                    family_id = family_result.get('data', {}).get('id')
                    print(f"Successfully created family with ID: {family_id}")
                    
                    # Then, add a baby to the family
                    baby_data = {
                        "familyId": family_id,
                        "name": "Test Baby",
                        "gender": "MALE",
                        "birthday": "2023-01-01",
                        "description": "A test baby for growth records"
                    }
                    
                    print("\nAttempting to add baby...")
                    try:
                        baby_response = requests.post(f"{base_url}/family/baby/add", json=baby_data, headers=headers)
                        print(f"Baby creation response status: {baby_response.status_code}")
                        print(f"Baby creation response body: {baby_response.text}")
                        
                        if baby_response.status_code == 200:
                            baby_result = baby_response.json()
                            baby_id = baby_result.get('data', {}).get('id')
                            print(f"Successfully created baby with ID: {baby_id}")
                            
                            # Finally, create a growth record
                            record_data = {
                                "babyId": baby_id,
                                "type": "DIARY",
                                "title": "First Growth Record",
                                "content": "This is a test growth record.",
                                "mediaUrls": [],
                                "tags": ["test", "first"]
                            }
                            
                            print("\nAttempting to create growth record...")
                            try:
                                record_response = requests.post(f"{base_url}/growth-record/create", json=record_data, headers=headers)
                                print(f"Record creation response status: {record_response.status_code}")
                                print(f"Record creation response body: {record_response.text}")
                                
                                if record_response.status_code == 200:
                                    print("Successfully created growth record!")
                                else:
                                    print("Failed to create growth record.")
                                    
                            except Exception as e:
                                print(f"Error during record creation: {e}")
                        else:
                            print("Failed to create baby.")
                            
                    except Exception as e:
                        print(f"Error during baby creation: {e}")
                else:
                    print("Failed to create family.")
                    
            except Exception as e:
                print(f"Error during family creation: {e}")
        else:
            print("Failed to extract token from login response.")
    else:
        print("Login failed.")
        
except Exception as e:
    print(f"Error during login: {e}")