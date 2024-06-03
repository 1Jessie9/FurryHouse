package com.furryhouse.furryhouse.Models

class ModelPartern {
    var id: String = ""
    var names: String = ""
    var experience: String = ""
    var price: String = ""
    var imgUrl: String = ""
    var location: String = ""

    constructor()

    constructor(id: String, names: String, experience: String, price: String, imgUrl: String, location: String) {
        this.id = id
        this.names = names
        this.experience = experience
        this.price = price
        this.imgUrl = imgUrl
        this.location = location
    }


}