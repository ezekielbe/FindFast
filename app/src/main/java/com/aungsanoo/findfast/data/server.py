from flask import Flask, jsonify, request
from pymongo import MongoClient
from bson import ObjectId
from datetime import datetime

import json

app = Flask(__name__)

client = MongoClient("mongodb+srv://csis4280theone:dXX3FFJbTLr7zSlg@cluster0.enal4.mongodb.net/csis4280project?retryWrites=true&w=majority&tlsAllowInvalidCertificates=true")

db = client.csis4280project
users_collection = db.users
products_collection = db.products
transactions_collection = db.transactions
#login
from flask import jsonify, request

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    phone = data.get("phone")
    email = data.get("email")
    role = data.get("role")
    isAdmin = data.get("isAdmin")
    currentTime = datetime.utcnow()

    # check if user exists
    user = users_collection.find_one({"username": username})
    if user:
        return jsonify({
            "status": False,
            "message": "User Already exists!"
        }), 400

    # Check if all fields are provided
    if not all([username, password, phone, email, role, isAdmin is not None]):
        return jsonify({
            "status": False,
            "message": "All fields are required: username, password, role, isAdmin, and wishlist."
        }), 400

    # Insert new user
    new_user = {
        "username": username,
        "password": password,
        "phone": phone,
        "email": email,
        "role": role,
        "isAdmin": isAdmin,
        "createdTime": currentTime,
        "updatedTime": currentTime
    }
    
    try:
        users_collection.insert_one(new_user)
        return jsonify({
            "status": True,
            "message": "User created successfully!",
            "userId": str(new_user["_id"]),
            "isAdmin": new_user.get("isAdmin", False)
        }), 201
    except Exception as e:
        return jsonify({
            "status": False,
            "message": f"An error occurred ${e}"
        }), 500

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")

    # Fetch user data from MongoDB
    user = users_collection.find_one({"username": username, "password": password})
    if user:
        return jsonify({
            "status": True,
            "message": "Login successful",
            "userId": str(user["_id"]),
            "isAdmin": user.get("isAdmin", False)
        }), 200
    else:
        return jsonify({
            "status": False,
            "message": "Invalid credentials"
        }), 401

@app.route('/user/<string:user_id>', methods=['GET'])
def get_user(user_id):

    try:
        user = users_collection.find_one({"_id": ObjectId(user_id)}, {"_id": 0})
        if user:
            user["status"] = True
            user["message"] = "User found"
            return jsonify(user), 200
        else:
            return jsonify({"status": False, "message": "User not found"}), 404
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

@app.route('/users', methods=['GET'])
def get_users():
    try:
        users = users_collection.find({}, {"_id": 0})
        user_list = list(users)
        return jsonify(user_list), 200
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

@app.route('/user/<string:user_id>', methods=['PUT'])
def update_user(user_id):
    try:
        data = request.get_json()

        foundUser = users_collection.find_one({"username": data.get("username")})
        editingUser = users_collection.find_one({"_id": ObjectId(user_id)}, {"_id": 0})

        if foundUser and foundUser["_id"] != ObjectId(user_id):
            return jsonify({
                "status": False,
                "message": "User Already exists!"
            }), 400

        updated_data = {
            "username": data.get("username"),
            "email": data.get("email"),
            "password": data.get("password"),
            "phone": data.get("phone"),
            "updatedTime": datetime.utcnow()
        }

        result = users_collection.update_one(
            {"_id": ObjectId(user_id)},
            {"$set": updated_data}
        )

        if result.matched_count > 0:
            updated_user = users_collection.find_one({"_id": ObjectId(user_id)}, {"_id": 0})
            updated_user["status"] = True
            updated_user["message"] = "User updated successfully"
            return jsonify(updated_user), 200
        else:
            return jsonify({"status": False, "message": "User not found"}), 404

    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500


if __name__ == '__main__':
    app.run(host="0.0.0.0", port="8888",debug=True)
