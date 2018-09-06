#!/bin/sh

echo "Synchronisation des sources vers SVN"

export LC_CTYPE=en_US.UTF-8 # Forcer l'encodage en UTF-8 (pour les commentaires)

WORKSPACE_HOME="$(dirname "$PWD")"
SVN_HOME="$WORKSPACE_HOME"/svn

if [ $# != 1 ] && [ $# != 2 ]; then
    echo "[ERREUR] Missing project arguments"
    exit 0
fi

if [ -f $2 ]; then
  echo "[INFO] Pas de second paramètre utilisation de la branche develop"
  branche="develop"
  master="master"
elif [[ $2 =~ ^[A-Za-z] ]]; then 
  branche="$2"
  master="$2"
  echo "[INFO] Utilisation de la branche git $branche"
else
  echo "[ERREUR] Le second paramètre ne correspond pas à nom de branche"
  exit 0
fi

cd "$WORKSPACE_HOME"/"$1" # Déplacement dans le répertoire Git à analyser

echo "=> Récupération de l'url svn"
# svn_url=`git config --get svn-remote.svn.url`
svn_url="https://dev.lutece.paris.fr/svn/non_lutece/R57-DansMaVille-Android"

if [[ -z "$svn_url" ]]; then
  echo "[ERREUR] URL SVN non définie dans la configuration de GIT"
  exit 1
fi

#Si l'url ne contient pas déjà le chemin vers le trunk on l'ajoute
if [[ $svn_url != *"/trunk"* ]]; then
  svn_url="$svn_url/trunk"
fi

echo "[INFO ] URL SVN du projet $1 : $svn_url"

echo "---------------------------------------"
echo "Fetch Source Gitlab"
echo "---------------------------------------"

echo "=> Checking the git repo is up to date..."
git checkout $master
last_commit=`git log --pretty=format:"%h" | awk 'NR==1'` #Sauvergarde du dernier commit pris en compte
git fetch
git reset --hard origin/$master

echo "=> Récupération des tickets pour le message du commit"
echo "Synchronization des commits GIT sur SVN" >> "$WORKSPACE_HOME"/svn_commit_msg_"$1".txt
git log --pretty=format:"%s" "$last_commit"...HEAD >> ./git_commit.log # Stockage des messages de commit
while read line; do
    echo $line >> "$WORKSPACE_HOME"/svn_commit_msg_"$1".txt
done <./git_commit.log
rm ./git_commit.log # Suppression du fichier temporaire

echo "---------------------------------------"
echo "Synchronization des commits GIT sur SVN"
echo "---------------------------------------"

echo "=> Checkout source from SVN..."
mkdir -p $SVN_HOME/"$1"
cd $SVN_HOME/"$1"
svn checkout "$svn_url" . --username AAH --password oZRE3R --no-auth-cache
rm -Rf *
cp -Rf "$WORKSPACE_HOME"/"$1"/* ./
svn add --force .
echo "=> Ajout des fichiers supprimés"
# svn status | grep '^!' | sed 's/! *//' | xargs -d '\n' svn delete --force
echo "=> Commit SVN"
svn commit --username AAH --password oZRE3R --no-auth-cache -F "$WORKSPACE_HOME"/svn_commit_msg_"$1".txt

echo "=> Nettoyage des fichiers et répertoires temporaires"
cd "$WORKSPACE_HOME"/"$1"
rm -f "$WORKSPACE_HOME"/svn_commit_msg_"$1".txt
# git checkout $branche
# 
# echo "[INFO ] Dernier commit trouvé : $last_commit"

#END
echo "=> DONE"
exit 0