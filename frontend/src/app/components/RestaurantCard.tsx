import { Card, CardContent, CardHeader } from "./ui/card";
import { Badge } from "./ui/badge";
import { RatingComponent } from "./RatingComponent";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { useSession } from "../contexts/SessionContext";
import { Heart } from "lucide-react";
import { useState } from "react";
import toast from "react-hot-toast";
import { fetchWithAuth } from "@/lib/api";
import ActionMenu from "./ActionMenu";

export interface Restaurant {
  id: string;
  name: string;
  cuisine: string;
  description: string;
  // rating: number;
  tags: string[];
  image: string;
  isFavourite?: boolean;
  // location: string;
  // distance: number;
  // hours: string;ad
  // phone: string;
  // priceRange: string;
}

interface RestaurantCardProps {
  restaurant: Restaurant;
  // onRatingChange: (restaurantId: string, rating: number) => void;
  onRemove: () => void;
  onFavouriteRemove: () => void;

}

export function RestaurantCard({
  restaurant,
  // onRatingChange,
  onRemove,
  onFavouriteRemove,
}: RestaurantCardProps) {
  const { session } = useSession();
  // const [liked, setLiked] = useState(false);
  const [isFavourite, setIsFavourite] = useState(restaurant.isFavourite ?? false);


    const onDelete = async () => {
    if (!session.token) {
      toast.error("You must be logged in to delete a Restaurant.");
      return;
    }

    const url = `${process.env.NEXT_PUBLIC_API_URL}/foodapp/restaurant/${restaurant.id}`;

    try {
      const response = await fetchWithAuth(url, {
        method: "DELETE",
      });
      
      if (response.ok) {
        toast.success(t => (
            <div onClick={() => toast.dismiss(t.id)} style={{ cursor: "pointer" }}>
              Restaurant deleted successfully!
            </div>
        ));
        onRemove();
      } else {
        const errorData = await response.json();
        toast.error(errorData.message || "Failed to delete the restaurant");
      }
    } catch (error) {
      toast.error(`${error} error occurred. Please try again.`);
    }
  }

  const handlePencilClick = () => {
    console.log("edit clicked")
  }


  const handleFavouriteToggle = async () => {
    if (!session.token) {
      toast.error("You must be logged in to favourite a restaurant.");
      return;
    }

    const endpoint = isFavourite ? "unFavourite" : "favourite";
    const url = `${process.env.NEXT_PUBLIC_API_URL}/foodapp/restaurant/${endpoint}/${restaurant.id}`;

    try {
      const response = await fetchWithAuth(url, {
        method: "POST",
        // headers: {
        //   Authorization: `Bearer ${session.token}`,
        // },
      });

      if (response.ok) {
        setIsFavourite(!isFavourite);
        if (isFavourite) {
          onFavouriteRemove(); // Call to remove dish from parent's state on unfavourite
        }
        toast.success(
            `Restaurant ${
                isFavourite ? "unfavourited" : "favourited"
            } successfully!`
        );
      } else {
        const errorData = await response.json();
        toast.error(errorData.message || "Failed to update favourite status.");
      }
    } catch (error) {
      toast.error(`${error} occurred. Please try again.`);
    }
  };
  return (
    <Card className="overflow-hidden shadow hover:shadow-md transition-shadow gap-0">
      <CardHeader className="p-0">
        <div className="relative w-full h-55">
          <ImageWithFallback
            src={restaurant.image}
            alt={restaurant.name}
            className="object-cover"
          />
          {session.isLoggedIn && (
            <div
              className="absolute top-2 right-2 bg-white rounded-full p-1.5 cursor-pointer"
              onClick={handleFavouriteToggle}
            >
              <Heart
                className={`w-6 h-6 ${
                    isFavourite ? "text-red-500 fill-current" : "text-gray-500"
                }`}
              />
            </div>
          )}
        </div>
      </CardHeader>
      <CardContent className="px-4">
        <div className="space-y-1">
          <div className="flex justify-between items-start space-y-0">
            <div>
              <h3 className="font-semibold">{restaurant.name}</h3>
              <p className="text-sm text-muted-foreground">
                {restaurant.cuisine}
              </p>
            </div>

            <div>
              <ActionMenu onEdit={handlePencilClick} onDelete={onDelete} />
              {/* <ActionMenu onDelete={onDelete}  /> */}
            </div>
          </div>

          <p className="text-sm text-gray-600 line-clamp-2">
            {restaurant.description}
          </p>

          {/*<div className="grid grid-cols-2 gap-2 text-sm text-muted-foreground">*/}
          {/*  <div className="flex items-center space-x-1">*/}
          {/*    <MapPin className="h-3 w-3" />*/}
          {/*    <span>{restaurant.distance} mi</span>*/}
          {/*  </div>*/}
          {/*  <div className="flex items-center space-x-1">*/}
          {/*    <Clock className="h-3 w-3" />*/}
          {/*    <span>{restaurant.hours}</span>*/}
          {/*  </div>*/}
          {/*</div>*/}

          {/*<div className="flex items-center justify-between">*/}
          {/*  <span className="font-medium text-green-600">{restaurant.priceRange}</span>*/}
          {/*  <a*/}
          {/*    href={`tel:${restaurant.phone}`}*/}
          {/*    className="flex items-center space-x-1 text-sm text-muted-foreground hover:text-primary"*/}
          {/*  >*/}
          {/*    <Phone className="h-3 w-3" />*/}
          {/*    <span>{restaurant.phone}</span>*/}
          {/*  </a>*/}
          {/*</div>*/}

          <div className="flex flex-wrap gap-1">
            {restaurant.tags.slice(0, 3).map((tag) => (
              <Badge key={tag} className="text-xs ">
                {tag}
              </Badge>
            ))}
          </div>

          {/* <div className="pt-2">
            <RatingComponent
              currentRating={restaurant.rating}
              onRatingChange={(rating) =>
                onRatingChange(restaurant.id, rating)
              }
            />
          </div> */}
        </div>
      </CardContent>
    </Card>
  );
}
