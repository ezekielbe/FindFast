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
cart_collection = db.cart
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

    user = users_collection.find_one({"username": username})
    if user:
        return jsonify({
            "status": False,
            "message": "User Already exists!"
        }), 400

    if not all([username, password, phone, email, role, isAdmin is not None]):
        return jsonify({
            "status": False,
            "message": "All fields are required: username, password, role, isAdmin, and wishlist."
        }), 400

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


@app.route('/products', methods=['GET'])
def get_products():
    products = []
    for product in products_collection.find({}):
        products.append({
            "id": str(product["_id"]),
            "name": product["name"],
            "price": product["price"],
            "description": product["description"],
            "material": product.get("material", [] if isinstance(product.get("material"), list) else [product.get("material")]),
            "color": product.get("color", [] if isinstance(product.get("color"), list) else [product.get("color")]),
            "size": product.get("size", [] if isinstance(product.get("size"), list) else [product.get("size")]),
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

    cart_item = db.cart.find_one({"user_id": user_id, "product_id": product_id})

    if cart_item:
        db.cart.update_one(
            {"user_id": user_id, "product_id": product_id},
            {"$set": {"quantity": quantity}}
        )
    else:
        db.cart.insert_one({
            "user_id": user_id,
            "product_id": product_id,
            "quantity": quantity
        })

    return jsonify({"status": True, "message": "Cart updated successfully"}), 200


@app.route('/get_cart_items', methods=['GET'])
def get_cart_items():
    user_id = request.args.get("user_id")
    if not user_id:
        return jsonify({"status": False, "message": "User ID not provided"}), 400

    cart_items = cart_collection.find({"user_id": user_id})
    items = []

    for item in cart_items:
        product_id = item.get("product_id")
        quantity = item.get("quantity", 1)

        # Fetch product details for each cart item
        product = products_collection.find_one({"_id": ObjectId(product_id)})

        if product:
            items.append({
                "user_id": user_id,  # Ensure user_id is included
                "product_id": str(product["_id"]),  # Ensure product_id is included
                "productName": product.get("name"),
                "productPrice": product.get("price"),
                "quantity": quantity
            })
        else:
            items.append({
                "user_id": user_id,  # Include user_id even if product is missing
                "product_id": product_id,
                "productName": None,
                "productPrice": None,
                "quantity": quantity
            })

    return jsonify(items), 200

@app.route('/delete_cart_item', methods=['DELETE'])
def delete_cart_item():
    user_id = request.args.get("user_id")
    product_id = request.args.get("product_id")

    if not user_id or not product_id:
        return jsonify({"status": False, "message": "User ID or Product ID missing"}), 400


    db.cart.delete_one({"user_id": user_id, "product_id": product_id})

    return jsonify({"status": True, "message": "Item removed from cart"}), 200


@app.route('/checkout', methods=['POST'])
def checkout():
    data = request.get_json()
    user_id = data.get("user_id")

    if not user_id:
        return jsonify({"status": False, "message": "User ID not provided"}), 400

    # Retrieve all items in the user's cart
    cart_items = list(cart_collection.find({"user_id": user_id}))  # Convert cursor to list
    if len(cart_items) == 0:
        return jsonify({"status": False, "message": "No items in cart"}), 400

    # Build the transaction details
    products = []
    total_amount = 0.0
    for item in cart_items:
        product_id = item["product_id"]
        quantity = item["quantity"]

        # Fetch product details
        product = products_collection.find_one({"_id": ObjectId(product_id)})
        if product:
            products.append({
                "id": str(product["_id"]),
                "name": product["name"],
                "quantity": quantity
            })
            total_amount += product["price"] * quantity

    # Create a new transaction document
    transaction = {
        "user_id": user_id,
        "products": products,
        "checkout_date": datetime.utcnow(),
        "total": total_amount
    }
    transactions_collection.insert_one(transaction)

    # Clear the user's cart
    cart_collection.delete_many({"user_id": user_id})

    return jsonify({"status": True, "message": "Checkout successful", "transaction_id": str(transaction["_id"])}), 200



@app.route('/products/<string:product_id>', methods=['GET'])
def get_product_by_id(product_id):
    try:
        product = products_collection.find_one({"_id": ObjectId(product_id)})
        if product:
            product_data = {
                "id": str(product["_id"]),
                "name": product["name"],
                "price": product["price"],
                "description": product["description"],
                "material": product.get("material", [] if isinstance(product.get("material"), list) else [product.get("material")]),
                "color": product.get("color", [] if isinstance(product.get("color"), list) else [product.get("color")]),
                "size": product.get("size", [] if isinstance(product.get("size"), list) else [product.get("size")]),
                "availability": product["availability"],
                "qty": product["qty"],
                "aisle": product["aisle"],
                "type": product["type"],
                "shelf": product["shelf"],
                "bin": product["bin"]
            }
            return jsonify(product_data), 200
        else:
            return jsonify({"status": False, "message": "Product not found"}), 404
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

@app.route('/products/<string:product_id>', methods=['PUT'])
def update_product(product_id):
    data = request.get_json()

    if not data:
        return jsonify({"status": False, "message": "No data provided"}), 400

    # Ensure material, color, and size fields are stored as lists
    updated_fields = {
        "name": data.get("name"),
        "price": data.get("price"),
        "description": data.get("description"),
        "material": data.get("material") if isinstance(data.get("material"), list) else [data.get("material")],
        "color": data.get("color") if isinstance(data.get("color"), list) else [data.get("color")],
        "size": data.get("size") if isinstance(data.get("size"), list) else [data.get("size")],
        "availability": data.get("availability"),
    }

    updated_fields = {k: v for k, v in updated_fields.items() if v is not None}

    result = products_collection.update_one(
        {"_id": ObjectId(product_id)},
        {"$set": updated_fields}
    )

    if result.matched_count > 0:
        return jsonify({"status": True, "message": "Product updated successfully"}), 200
    else:
        return jsonify({"status": False, "message": "Product not found"}), 404
@app.route('/products/<string:product_id>', methods=['DELETE'])
def delete_product(product_id):
    try:
        result = products_collection.delete_one({"_id": ObjectId(product_id)})
        if result.deleted_count > 0:
            return jsonify({"status": True, "message": "Product deleted successfully"}), 200
        else:
            return jsonify({"status": False, "message": "Product not found"}), 404
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500
@app.route('/products', methods=['POST'])
def add_product():
    data = request.get_json()
    new_product = {
        "name": data["name"],
        "price": data["price"],
        "description": data["description"],
        "material": data["material"],
        "color": data["color"],
        "size": data["size"],
        "availability": data["availability"],
        "qty": data["qty"],
        "aisle": data["aisle"],
        "type": data["type"],
        "shelf": data["shelf"],
        "bin": data["bin"]
    }
    try:
        products_collection.insert_one(new_product)
        return jsonify({"status": True, "message": "Product added successfully"}), 201
    except Exception as e:
        return jsonify({"status": False, "message": str(e)}), 500

# Get all transactions for report
@app.route('/transactions', methods=['GET'])
def transactions():
    try:
        transactions = transactions_collection.find({}, {"_id": 0})
        transaction_list = list(transactions)
        return jsonify(transaction_list), 200
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

# Get transactions of a user
@app.route('/transactions/<string:user_id>', methods=['GET'])
def transactions_by_user(user_id):
    try:
        transactions = transactions_collection.find({"user_id": user_id}, {"_id": 0})
        transaction_list = list(transactions)
        return jsonify(transaction_list), 200
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

if __name__ == '__main__':
    app.run(host="0.0.0.0", port="8888",debug=True)
