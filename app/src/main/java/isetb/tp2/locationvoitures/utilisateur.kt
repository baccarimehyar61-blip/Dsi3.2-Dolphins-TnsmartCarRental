package isetb.tp2.locationvoitures

data class utilisateur(
    var id: Int? = null,
    var nom: String? = null,
    var prenom: String? = null,
    var email: String,
    var motDePasse: String,
    var telephone: String? = null
)