<?php

namespace App\Entity;

use App\Repository\CreationRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: CreationRepository::class)]
#[ORM\HasLifecycleCallbacks]
class Creation
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;
    
    #[ORM\ManyToOne(targetEntity: Utilisateur::class, inversedBy: 'creations')]
    #[ORM\JoinColumn(nullable: false)]
    private ?Utilisateur $utilisateur = null;

    #[ORM\Column(length: 100)]
    #[Assert\NotBlank(message: "Le titre ne peut pas être vide")]
    #[Assert\Length(
        min: 2,
        max: 100,
        minMessage: "Le titre doit contenir au moins {{ limit }} caractères",
        maxMessage: "Le titre ne peut pas dépasser {{ limit }} caractères"
    )]
    private ?string $titre = null;

    #[ORM\Column(type: 'text', nullable: true)]
    private ?string $description = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $image = null;

    #[ORM\Column(length: 100)]
    #[Assert\NotBlank(message: "La catégorie est requise")]
    private ?string $categorie = null;

    #[ORM\Column(type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $datePublic = null;

    #[ORM\Column(length: 100)]
    #[Assert\Choice(choices: ["actif", "inactif", "en_attente"], message: "Statut invalide")]
    private ?string $statut = "actif";

    #[ORM\Column]
    private ?int $nbLike = 0;

    #[ORM\OneToMany(targetEntity: Commentaire::class, mappedBy: 'creation', cascade: ['persist', 'remove'])]
    private Collection $commentaires;

    private $imageFile;

    public function __construct()
    {
        $this->commentaires = new ArrayCollection();
        $this->datePublic = new \DateTime();
        $this->nbLike = 0;
        $this->statut = 'actif';
    }

    #[ORM\PrePersist]
    #[ORM\PreUpdate]
    public function setUpdatedAtValue(): void
    {
        if ($this->datePublic === null) {
            $this->datePublic = new \DateTime();
        }
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getTitre(): ?string
    {
        return $this->titre;
    }

    public function setTitre(string $titre): static
    {
        $this->titre = $titre;
        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(?string $description): static
    {
        $this->description = $description;
        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): static
    {
        $this->image = $image;
        return $this;
    }

    public function getImageFile()
    {
        return $this->imageFile;
    }

    public function setImageFile($imageFile): static
    {
        $this->imageFile = $imageFile;
        if ($imageFile) {
            $this->datePublic = new \DateTime();
        }
        return $this;
    }

    public function getCategorie(): ?string
    {
        return $this->categorie;
    }

    public function setCategorie(string $categorie): static
    {
        $this->categorie = $categorie;
        return $this;
    }

    public function getDatePublic(): ?\DateTimeInterface
    {
        return $this->datePublic;
    }

    public function setDatePublic(\DateTimeInterface $datePublic): static
    {
        $this->datePublic = $datePublic;
        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;
        return $this;
    }

    public function getNbLike(): ?int
    {
        return $this->nbLike;
    }

    public function setNbLike(int $nbLike): static
    {
        $this->nbLike = $nbLike;
        return $this;
    }

    public function getCommentaires(): Collection
    {
        return $this->commentaires;
    }

    public function addCommentaire(Commentaire $commentaire): static
    {
        if (!$this->commentaires->contains($commentaire)) {
            $this->commentaires->add($commentaire);
            $commentaire->setCreation($this);
        }
        return $this;
    }

    public function removeCommentaire(Commentaire $commentaire): static
    {
        if ($this->commentaires->removeElement($commentaire)) {
            if ($commentaire->getCreation() === $this) {
                $commentaire->setCreation(null);
            }
        }
        return $this;
    }

    // Getter and setter for Utilisateur relationship removed
}
