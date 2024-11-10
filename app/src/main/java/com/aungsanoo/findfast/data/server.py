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
    role = data.get("role")
    isAdmin = data.get("isAdmin")
    currentTime = datetime.utcnow()
    defaultAddress = data.get("defaultAddress")

    # check if user exists
    user = users_collection.find_one({"username": username})
    if user:
        return jsonify({
            "status": False,
            "message": "User Already exists!"
        }), 400

    # Check if all fields are provided
    if not all([username, password, phone, role, isAdmin is not None]):
        return jsonify({
            "status": False,
            "message": "All fields are required: username, password, role, isAdmin, and wishlist."
        }), 400

    # Insert new user
    new_user = {
        "username": username,
        "password": password,
        "phone": phone,
        "role": role,
        "isAdmin": isAdmin,
        "createdTime": currentTime,
        "updatedTime": currentTime,
        "defaultAddress": defaultAddress
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
            "userId": str(user["_id"]),
            "isAdmin": user.get("isAdmin", False),
            "message": "Login successful"
        }), 200
    else:
        return jsonify({
            "status": "error",
            "message": "Invalid credentials"
        }), 401

@app.route('/products', methods=['GET'])
def get_products():
    name = request.args.get('name')
    material = request.args.get('material')
    price_range = request.args.get('priceRange')

    query = {}

    if name:
        query['name'] = {'$regex': name, '$options': 'i'}  # case-insensitive search
    if material:
        query['material'] = material
    if price_range:
        min_price, max_price = map(float, price_range.split('-'))
        query['price'] = {'$gte': min_price, '$lte': max_price}

    products = []
    for product in products_collection.find(query):
        products.append({
            "id": str(product["_id"]),
            "name": product["name"],
            "price": product["price"],
            "description": product["description"],
            "material": product["material"],
            "color": product.get("color", []),
            "size": product.get("size", []),
            "availability": product["availability"],
            "qty": product["qty"],
            "aisle": product["aisle"],
            "type": product["type"],
            "shelf": product["shelf"],
            "bin": product["bin"]
        })
    return jsonify(products), 200
@app.route('/update_cart', methods=['POST'])
def update_cart():
    data = request.get_json()
    user_id = data.get("user_id")
    product_id = data.get("product_id")
    quantity = data.get("quantity")

    if not all([user_id, product_id, quantity]):
        return jsonify({"status": False, "message": "Missing data"}), 400

    user = users_collection.find_one({"_id": ObjectId(user_id)})
    if user:
        cart = user.get("cart", {})
        cart[str(product_id)] = quantity
        users_collection.update_one(
            {"_id": ObjectId(user_id)},
            {"$set": {"cart": cart}}
        )
        return jsonify({"status": True, "message": "Cart updated successfully"}), 200
    else:
        return jsonify({"status": False, "message": "User not found"}), 404

if __name__ == '__main__':
    app.run(host="0.0.0.0", port="8888",debug=True)
