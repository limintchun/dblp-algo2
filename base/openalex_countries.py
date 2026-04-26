import matplotlib.pyplot  as plt
import matplotlib.ticker  as mtick
import csv
import os
import json


from urllib.request import urlopen
from urllib.parse   import quote


CSV_PATH                           = "top10_scc_authors.csv"
RESULTS_DIRECTORY_PATH             = "../results/plots"
COUNTRIES_AFFILIATIONS_PNG_PATH    = "../results/plots/countries_affiliations.png"
COUNTRIES_PROPORTIONS_PNG_PATH     = "../results/plots/countries_proportions.png"


def read_csv():
  communities = {}

  with open(CSV_PATH, "r") as f:
    reader = csv.DictReader(f)

    for row in reader:
      community_rank  = int(row["community_rank"])
      author          = row["author"]

      if community_rank not in communities:
        communities[community_rank] = []

      communities[community_rank].append(author)

  return communities


# Fonction pour supprimer les suffixes provenant de DBLP
# OpenAlex peut faire échouer une recherche (ex. "Hyuk Park 0001" / "Hyuk Park")
def clean_author_name(author_name):
  # Sépare le nom en une liste de mots
  words_name = author_name.split()

  # Si le dernier mot est un nombre alors il est supprimé
  if words_name[-1].isdigit():
    words_name = words_name[:-1]

  # Reconstruit la chaîne de caractères à partir de la liste de mots
  return " ".join(words_name)


# Fonction pour extraire le pays d'un auteur à partir des données renvoyées par OpenAlex
def extract_country(author_data):
  country = "Unknown"

  # Liste des dernières institutions
  institutions = author_data.get("last_known_institutions")

  if institutions:
    # Dernière institution où l'auteur a travaillé en premier
    institution = institutions[0]
    country     = institution.get("country_code", "Unknown")
  else:
    # Si aucun résultat, on regarde les affiliations
    affiliations = author_data.get("affiliations")

    if affiliations:
      # On prend la dernière institution affiliée
      affiliated_institution = affiliations[0].get("institution")

      if affiliated_institution:
        country = affiliated_institution.get("country_code", "Unknown")

  return country


def get_country(author_name):
  country = "Unknown" # Valeur inconnue par défaut

  # Construction de l'URL pour recherche du nom de l'auteur
  # quote() remplace les espaces par %20 pour éviter des URLs invalides
  url = "https://api.openalex.org/authors?search=" + quote(author_name) + "&per-page=1"

  with urlopen(url) as openalex_response:
    # Lecture de la réponse (JSON) et conversion en dictionnaire
    data = json.loads(openalex_response.read().decode("utf-8"))

  # Liste des auteurs trouvés
  results = data["results"]

  # Si aucun auteur trouvé alors résultat inconnu
  if len(results) > 0:
    # On prend le premier résultat (on ne considère que le premier comme pertinent)
    author    = results[0]
    country   = extract_country(author)

  return country


def count_countries_communities(communities):
  count = {}

  for community_rank in communities:
    count[community_rank] = {}

    for author_name in communities[community_rank]:
      # Nettoyage du nom DBLP avant la recherche OpenAlex
      clean_name  = clean_author_name(author_name)

      country     = get_country(clean_name)

      if country not in count[community_rank]:
        count[community_rank][country] = 0

      count[community_rank][country] += 1

  return count


def compute_countries_proportions_communities(count):
  proportions = {}

  for community in count:
    proportions[community] = {}

    # Total d’auteurs dans la communauté
    total = sum(count[community].values())

    for country in count[community]:
      proportions[community][country] = count[community][country] / total

  return proportions


def plot_countries_affiliations_by_communities(count):
  os.makedirs(RESULTS_DIRECTORY_PATH, exist_ok=True)

  table_data = []

  for community_rank in count:
    countries       = []
    countries_text  = ""
    affiliation     = ""

    for country in count[community_rank]:
      if country == "Unknown":
        countries.append("PAYS INCONNU")
      else:
        countries.append(country)

    if len(countries) == 0:
      countries_text  = "Aucune valeur"
      affiliation     = "Aucune valeur"
    elif len(countries) == 1:
      countries_text  = countries[0]
      affiliation     = "Association unique"
    else:
      countries_text  = ", ".join(countries)
      affiliation     = "Association multiple"

    table_data.append([community_rank, countries_text, affiliation])

  fig, ax = plt.subplots(figsize=(14, 5))
  ax.axis("off")
  table = ax.table(
      cellText=table_data,
      colLabels=["Communautés", "Liste de pays", "Associations"],
      loc="center")

  table.auto_set_font_size(False)
  table.set_fontsize(10)
  table.scale(1, 1.5)
  table.auto_set_column_width(col=[0, 1, 2])

  plt.title("Association des 10 plus grandes communautés à un ou plusieurs pays")

  plt.tight_layout()
  plt.savefig(COUNTRIES_AFFILIATIONS_PNG_PATH, dpi=150)
  plt.close()


# Sources :
# https://stackoverflow.com/questions/75165383/how-make-stacked-bar-chart-from-dataframe-in-python
# https://stackoverflow.com/questions/44309507/stacked-bar-plot-using-matplotlib
# https://www.youtube.com/watch?v=f7pxyGIaPkQ
# https://stackoverflow.com/questions/74727435/create-a-stacked-bar-plot-of-percentages-and-annotate-with-count
def plot_countries_proportions_by_communities(proportions):
  os.makedirs(RESULTS_DIRECTORY_PATH, exist_ok=True)

  communities = list(proportions.keys())

  # Récupération de tous les pays
  # Sert de base globale pour comparer toutes les communautés avec les mêmes pays
  all_countries = set()
  for community in proportions:
    for country in proportions[community]:
      if country == "Unknown":
        all_countries.add("PAYS INCONNU")
      else:
        all_countries.add(country)

  # Trier pour avoir un ordre et garantir une cohérence visuelle
  all_countries = sorted(all_countries)

  plt.figure(figsize=(16, 8))

  left = [0] * len(communities) # Graphique hozirontal

  # Pour chaque pays, on ajoute une "couche" dans les bâtonnets
  for country in all_countries:
    values = []

    # Récupérer la proportion du pays pour chaque communauté
    # Si un pays n'est pas présent dans la communauté, on met 0
    for community in communities:
      if country == "PAYS INCONNU":
        values.append(proportions[community].get("Unknown", 0))
      else:
        values.append(proportions[community].get(country, 0))

    # Bâtonnets à l'horizontal
    bars = plt.barh(
      communities,
      values,
      left=left,
      label=country,
      edgecolor="black",
      linewidth=0.5)

    for i, bar in enumerate(bars):
      # Choix arbitraire de ne pas afficher sur des blocs trop petits
      if values[i] > 0.05:
        percent = values[i] * 100
        plt.text(
          left[i] + values[i] / 2,
          communities[i],
          f"{country}\n{percent:.2f}%", # Proportions limitées à 2 décimales
          ha="center",
          va="center",
          fontsize=8)

    for i in range(len(left)):
      left[i] += values[i]

  plt.ylabel("Communautés")
  plt.yticks(communities)

  plt.xlabel("Proportion (%)")
  plt.xlim(0, 1) # Toutes les bâtonnets du stacked bar plot vont jusqu’à 100%
  plt.xticks([i / 10 for i in range(11)])
  plt.gca().xaxis.set_major_formatter(mtick.PercentFormatter(1.0))

  plt.grid(axis="x", linestyle="--", alpha=0.5)

  plt.title("Proportion d’auteur-rice-s travaillant dans différents pays")
  plt.legend(title="Pays", bbox_to_anchor=(1.02, 1), loc="upper left")

  plt.tight_layout(rect=[0, 0, 0.75, 1])
  plt.savefig(COUNTRIES_PROPORTIONS_PNG_PATH, dpi=150, bbox_inches="tight")
  plt.close()


def main():
  communities   = read_csv()
  count         = count_countries_communities(communities)
  proportions   = compute_countries_proportions_communities(count)

  plot_countries_affiliations_by_communities(count)
  plot_countries_proportions_by_communities(proportions)


if __name__ == "__main__":
  main()