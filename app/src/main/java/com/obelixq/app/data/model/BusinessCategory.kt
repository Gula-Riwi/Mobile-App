package com.obelixq.app.data.model

/**
 * Enum para las categorías de negocios en ObelixQ
 *
 * Estas son las categorías que aparecerán en el filtro de la HomeScreen
 */
enum class BusinessCategory(val displayName: String) {
    BARBERSHOP("Barbería"),
    SPA("Spa & Belleza"),
    LAWYER("Abogado"),
    CONSULTANT("Consultoría"),
    DENTIST("Odontología"),
    PSYCHOLOGIST("Psicología"),
    VETERINARY("Veterinaria"),
    MECHANIC("Mecánica"),
    OTHER("Otro")
}
