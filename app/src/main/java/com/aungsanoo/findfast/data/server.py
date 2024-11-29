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

        # Check if username is unique
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
            "street_address": data.get("street_address"),
            "city": data.get("city"),
            "state": data.get("state"),
            "postal_code": data.get("postal_code"),
            "country": data.get("country"),
            "card_number": data.get("card_number"),
            "card_expiry": data.get("card_expiry"),
            "card_cvv": data.get("card_cvv"),
            "updatedTime": datetime.utcnow()
        }

        # Remove any None values to avoid setting empty fields
        updated_data = {k: v for k, v in updated_data.items() if v is not None}

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
            "bin": product["bin"],
            "imageUrl": product.get("imageUrl","")

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

    product = products_collection.find_one({"_id": ObjectId(product_id)})

    if not product:
        return jsonify({"status": False, "message": "Product not found"}), 404

    cart_item = cart_collection.find_one({"user_id": user_id, "product_id": product_id})


    product_data = {
        "user_id": user_id,
        "product_id": product_id,
        "quantity": quantity,
        "productName": product["name"],
        "productPrice": product["price"],
        "productImageUrl": product.get("imageUrl", "")
    }

    if cart_item:
        cart_collection.update_one(
            {"user_id": user_id, "product_id": product_id},
            {"$set": product_data}
        )
    else:
        cart_collection.insert_one(product_data)

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

        product = products_collection.find_one({"_id": ObjectId(product_id)})

        if product:
            items.append({
                "user_id": user_id,
                "product_id": str(product["_id"]),
                "productName": product.get("name"),
                "productPrice": product.get("price"),
                "quantity": quantity,
                "productImageUrl": product.get("imageUrl","")
            })
        else:
            items.append({
                "user_id": user_id,
                "product_id": product_id,
                "productName": None,
                "productPrice": None,
                "quantity": quantity,
                "productImageUrl": None
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
            if product.get("qty", 0) < quantity:
                return jsonify({"status": False, "message": f"Insufficient quantity for product {product['name']}"}), 400

            products.append({
                "id": str(product["_id"]),
                "name": product["name"],
                "quantity": quantity,
                "price": product["price"],
                "aisle": product["aisle"],
                "bin": product["bin"],
                "imageUrl": product.get("imageUrl", None),
                "description": product["description"],
                "qty": product["qty"],
                "shelf": product["shelf"]
            })
            total_amount += product["price"] * quantity

            # Decrement the product quantity in the product collection
            new_quantity = product["qty"] - quantity
            products_collection.update_one(
                {"_id": ObjectId(product_id)},
                {"$set": {"qty": new_quantity}}
            )

    # Create a new transaction document
    transaction = {
        "user_id": user_id,
        "products": products,
        "checkout_date": datetime.utcnow(),
        "total": total_amount,
        "status": 0,
        "orderMessage": ""
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
                "name": product.get("name", ""),
                "basePrice": product.get("basePrice", 0.0),
                "price": product.get("price", 0.0),
                "description": product.get("description", ""),
                "material": product.get("material", [] if isinstance(product.get("material"), list) else [product.get("material")]),
                "color": product.get("color", [] if isinstance(product.get("color"), list) else [product.get("color")]),
                "size": product.get("size", [] if isinstance(product.get("size"), list) else [product.get("size")]),
                "availability": product.get("availability", "Unavailable"),
                "qty": product.get("qty", 0),
                "aisle": product.get("aisle", ""),
                "type": product.get("type", ""),
                "shelf": product.get("shelf", ""),
                "bin": product.get("bin", ""),
                "imageUrl": product.get("imageUrl", "")
            }
            return jsonify(product_data), 200
        else:
            return jsonify({"status": False, "message": "Product not found"}), 404
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

@app.route('/products/<string:product_id>', methods=['PUT'])
def update_product(product_id):
    data = request.get_json()
    updated_fields = {
        "name": data.get("name"),
        "price": data.get("price"),
        "basePrice": data.get("basePrice"),
        "description": data.get("description"),
        "material": data.get("material") if isinstance(data.get("material"), list) else [data.get("material")],
        "color": data.get("color") if isinstance(data.get("color"), list) else [data.get("color")],
        "size": data.get("size") if isinstance(data.get("size"), list) else [data.get("size")],
        "availability": data.get("availability"),
        "imageUrl": data.get("imageUrl")
    }

    # Remove any None values, to avoid setting empty fields
    updated_fields = {k: v for k, v in updated_fields.items() if v is not None}

    # Debugging: Print the updated fields to verify correctness
    print(f"Updating product {product_id} with fields: {updated_fields}")

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
        "basePrice": data.get("basePrice", 0.0),
        "description": data["description"],
        "material": data["material"],
        "color": data["color"],
        "size": data["size"],
        "availability": data["availability"],
        "qty": data["qty"],
        "aisle": data["aisle"],
        "type": data["type"],
        "shelf": data["shelf"],
        "bin": data["bin"],
        "imageUrl": data.get("imageUrl")
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
        transactions = transactions_collection.find({})
        transaction_list = list(transactions)
        response = []
        for transaction in transaction_list:
            response.append({
                "_id": str(transaction["_id"]),
                "checkout_date": transaction.get("checkout_date"),
                "products": transaction.get("products"),
                "total": transaction.get("total"),
                "user_id": transaction.get("user_id"),
                "updatedTime": transaction.get("updatedTime", transaction.get("checkout_date")),
                "status": transaction.get("status", 0),
                "orderMessage": transaction.get("orderMessage", "")
            })

        sorted_transactions = sorted(response, key=lambda x: x.get('updatedTime', ''), reverse=True)

        return jsonify(sorted_transactions), 200
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

# Get transactions of a user
@app.route('/transactions/<string:user_id>', methods=['GET'])
def transactions_by_user(user_id):
    try:
        transactions = transactions_collection.find({"user_id": user_id})
        transaction_list = list(transactions)
        response = []
        for transaction in transaction_list:
            response.append({
                "_id": str(transaction["_id"]),
                "checkout_date": transaction.get("checkout_date"),
                "products": transaction.get("products"),
                "total": transaction.get("total"),
                "user_id": transaction.get("user_id"),
                "updatedTime": transaction.get("updatedTime", transaction.get("checkout_date")),
                "status": transaction.get("status", 0),
                "orderMessage": transaction.get("orderMessage", "")
            })

        sorted_transactions = sorted(response, key=lambda x: x.get('updatedTime', ''), reverse=True)
        return jsonify(sorted_transactions), 200
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

# Update transaction by id (Order status update)
@app.route('/transactions/<string:transaction_id>/<int:status>', methods=['PUT'])
def order_update(transaction_id, status):
    try:
        transaction = transactions_collection.find_one({"_id": ObjectId(transaction_id)})

        if not transaction:
            return jsonify({"status": False, "message": "Order not found"}), 404

        if status < 0 or status > 4:
            return jsonify({"status": False, "message": "Invalid status value. It must be between 0 and 4."}), 400

        result = transactions_collection.update_one(
            {"_id": ObjectId(transaction_id)},
            {"$set": {"status": status, "updatedTime": datetime.utcnow()}}
        )

        if result.modified_count > 0:
            updated_transaction = transactions_collection.find_one({"_id": ObjectId(transaction_id)})
            updated_transaction["_id"] = transaction_id
            return jsonify(updated_transaction), 200
        else:
            return jsonify({"status": False, "message": "Order not found"}), 404
    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

# Cancel Order (=>add the products back also reason message to transaction obj)
@app.route('/cancel_order/<string:transaction_id>', methods=['PUT'])
def cancel_order(transaction_id):
    try:
        order_message = request.json.get("orderMessage", None)
        if not order_message:
            return jsonify({"status": False, "message": "Order message is required to cancel the order"}), 400

        transaction = transactions_collection.find_one({"_id": ObjectId(transaction_id)})
        
        if not transaction:
            return jsonify({"status": False, "message": "Order not found"}), 404
        
        if transaction['status'] == 4:
            return jsonify({"status": False, "message": "Order is already cancelled"}), 400
        
        transactions_collection.update_one(
            {"_id": ObjectId(transaction_id)},
            {"$set": {"status": 4, "orderMessage": order_message, "updatedTime": datetime.utcnow()}}
        )
        
        for product in transaction["products"]:
            product_id = product["id"]
            quantity = product["quantity"]
            
            product_record = products_collection.find_one({"_id": ObjectId(product_id)})
            
            if product_record:
                current_qty = product_record.get("qty", 0)
                updated_qty = current_qty + quantity
                
                # Update the product quantity back
                products_collection.update_one(
                    {"_id": ObjectId(product_id)},
                    {"$set": {"qty": updated_qty}}
                )
        
        return jsonify({"status": True, "message": "Order cancelled successfully", "orderMessage": order_message}), 200

    except Exception as e:
        return jsonify({"status": False, "error": str(e)}), 500

def serialize_transaction(transaction):
    """Converts ObjectId to string and serializes the transaction document."""
    transaction["_id"] = str(transaction["_id"])  # Convert ObjectId to string
    for product in transaction.get("products", []):
        if "_id" in product:  # Convert ObjectId in products if it exists
            product["_id"] = str(product["_id"])
    return transaction

@app.route('/financial_report', methods=['POST'])
def get_financial_report():
    try:
        data = request.get_json()
        month = data.get("month")
        year = data.get("year")

        if not month or not year:
            return jsonify({"error": "Month and year are required"}), 400

        if not (1 <= month <= 12):
            return jsonify({"error": "Invalid month"}), 400

        start_date = datetime(year, month, 1)
        if month == 12:
            end_date = datetime(year + 1, 1, 1)
        else:
            end_date = datetime(year, month + 1, 1)

        transactions = list(transactions_collection.find({
            "checkout_date": {"$gte": start_date, "$lt": end_date}
        }))

        transactions = [serialize_transaction(t) for t in transactions]

        manufactured_cost = 0
        total_revenue = 0
        total_items_sold = 0
        total_cost = 0
        operation_cost = len(transactions) * 10
        total_transactions = len(transactions)

        for transaction in transactions:
            total_revenue += transaction.get("total", 0)

            for product in transaction.get("products", []):
                manufactured_cost += product.get("price", 0) * product.get("quantity", 0)

            total_items_sold += sum(p.get("quantity", 0) for p in transaction.get("products", []))

        total_cost = manufactured_cost + operation_cost
        total_profit = total_revenue - total_cost

        if month == 1:
            prev_month_start = datetime(year - 1, 12, 1)
            prev_month_end = datetime(year, 1, 1)
        else:
            prev_month_start = datetime(year, month - 1, 1)
            prev_month_end = datetime(year, month, 1)

        prev_month_transactions = list(transactions_collection.find({
            "checkout_date": {"$gte": prev_month_start, "$lt": prev_month_end}
        }))
        prev_month_revenue = sum(t.get("total", 0) for t in prev_month_transactions)

        revenue_growth = (
            ((total_revenue - prev_month_revenue) / prev_month_revenue) * 100
            if prev_month_revenue > 0 else None
        )

        report = {
            "month": month,
            "year": year,
            "transactions": [t for t in transactions],
            "manufactured_cost": manufactured_cost,
            "operation_cost": operation_cost,
            "total_cost": total_cost,
            "total_revenue": total_revenue,
            "previous_month_revenue": prev_month_revenue,
            "total_profit": total_profit,
            "total_items_sold": total_items_sold,
            "revenue_growth": revenue_growth,
            "total_transactions": total_transactions
        }
        return jsonify(report), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/search', methods=['GET'])
def search_products():
    # Retrieve query parameters
    name = request.args.get("name")
    material = request.args.get("material")
    price_range = request.args.get("priceRange")

    # Initialize the query
    query = {}

    # Add name filter
    if name:
        query["name"] = {"$regex": f".*{name}.*", "$options": "i"}  # Case-insensitive regex match

    # Add material filter
    if material and material.lower() != "all":
        query["material"] = material

    # Add price range filter
    if price_range:
        try:
            # Expecting format: "min-max"
            min_price, max_price = map(float, price_range.split("-"))
            query["price"] = {"$gte": min_price, "$lte": max_price}
        except ValueError:
            return jsonify({"status": False, "message": "Invalid price range format. Use 'min-max'."}), 400

    # Debugging: Log the constructed query
    print(f"Incoming Params - Name: {name}, Material: {material}, Price Range: {price_range}")
    print(f"Constructed Query: {query}")

    try:
        # Execute query on MongoDB
        products = list(products_collection.find(query))

        # Convert ObjectId to string
        for product in products:
            product["_id"] = str(product["_id"])

        # Debugging: Log filtered products
        print(f"Filtered Products: {products}")

        # Return the filtered products
        return jsonify(products), 200

    except Exception as e:
        print(f"Error: {e}")
        return jsonify({"status": False, "message": str(e)}), 500


if __name__ == '__main__':
    app.run(host="0.0.0.0", port="8888",debug=True)
